package com.robusta.commons.export.service;

import com.robusta.commons.export.api.CellValue;
import com.robusta.commons.export.api.ExcelBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.springframework.util.ReflectionUtils.*;

/**
 * An {@link CellValue} annotation driven spreadsheet builder.
 *
 * Bean class/ Domain interface with accessor methods annotated
 * with {@link CellValue} are read into metadata.
 *
 * This metadata is used to populate header row and data rows
 * into a spreadsheet from a list of the Bean class/ Domain
 * interface implementation objects.
 *
 * <p>Metadata is reflectively read and cached during
 * instantiation, subsequent instantations for the same
 * bean/domain class reads from the metadata cache to
 * save reflective annotation reader calls.</p>
 * @param <Exportable>
 */
public abstract class AnnotatedParsingExcelBuilder<Exportable> implements ExcelBuilder<Exportable> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Class<Exportable> domainClass;
    private final List<CellValueMetadata> masterMetadataList;
    private static final Map<Class, List> cellValueMetadataCache = new ConcurrentHashMap<Class, List>();

    public AnnotatedParsingExcelBuilder(Class<Exportable> domainClass) {
        this.domainClass = domainClass;
        logger.debug("Reading Cell Value Annotations on the Domain class: '{}'", this.domainClass);
        masterMetadataList = readFromCache(domainClass);
    }

    private List<CellValueMetadata> readFromCache(Class<Exportable> domainClass) {
        final List<CellValueMetadata> metadataList;
        if(!cellValueMetadataCache.containsKey(domainClass)) {
            metadataList = readAnnotationsOnDomainClassAndPrepareMetadata(domainClass);
            cellValueMetadataCache.put(domainClass, metadataList);
            return metadataList;
        } else {
            //noinspection unchecked
            return cellValueMetadataCache.get(domainClass);
        }
    }

    /**
     * Reflectively read methods of the domain class/interface that are
     * annotated with {@link CellValue} and build a metadata list
     * of {@link CellValueMetadata} that is suitable for spreadsheet
     * rendering (generation).
     *
     * @param domainClass Class
     * @param <Domain> The domain class/interface or its derived
     *                class/interface
     * @return List&lt;CellValueMetadata&gt;
     */
    protected <Domain extends Exportable> List<CellValueMetadata> readAnnotationsOnDomainClassAndPrepareMetadata(Class<Domain> domainClass) {
        final List<CellValueMetadata> metadataList = newArrayList();
        final List<String> handledMethods = newArrayList();
        doWithMethods(domainClass,
                new MethodCallback() {
                    @Override
                    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                        logger.debug("Doing with method: '{}'", method);
                        if (method.isAnnotationPresent(CellValue.class)) {
                            logger.trace("Method is annotated with CellValue, proceeding to check sanity and collect metadata");
                            checkSanity(method);
                            CellValue annotation = method.getAnnotation(CellValue.class);
                            metadataList.add(newCellValueMetadata(method, annotation));
                            handledMethods.add(method.getName());
                        }
                    }
                },
                new MethodFilter() {
                    @Override
                    public boolean matches(Method method) {
                        return !handledMethods.contains(method.getName());
                    }
                }
        );
        Collections.sort(metadataList, OrderComparator.INSTANCE);
        logger.debug("Reading annotations completed on: '{}'. Size of the metadata list: '{}'", domainClass, metadataList.size());
        return metadataList;
    }

    /**
     * Allows building a {@link CellValueMetadata} from a bean class
     * method annotated with {@link CellValue}
     *
     * <p>Protected to allow sub-classes to build
     * {@link CellValueMetadata} or its sub-class in a custom manner.
     * </p>
     *
     * @param method Method the annotated method
     * @param annotation CellValue annotation
     * @return CellValueMetadata
     */
    protected CellValueMetadata newCellValueMetadata(Method method, CellValue annotation) {
        return new CellValueMetadata(annotation.position(), annotation.headerText(), annotation.visibility(), method);
    }

    private void checkSanity(Method method) {
        checkState(method.getReturnType().equals(String.class) && method.getParameterTypes().length == 0, "Non conforming method: %s", method);
        logger.trace("Conforming method to requirements - 1. Takes no parameters. 2. Returns a String.");
    }

    /**
     * Meta information class for {@link CellValue}
     */
    protected class CellValueMetadata implements Ordered {
        private final int position;
        private final String headerText;
        private final Method valueMethod;
        private final boolean visibility;

        private CellValueMetadata(int position, String headerText, boolean visibility, Method valueMethod) {
            this.position = position;
            this.headerText = headerText;
            this.valueMethod = valueMethod;
            this.visibility = visibility;
            this.valueMethod.setAccessible(true);
        }

        public String valueFrom(Exportable exportable) {
            return (String) invokeMethod(valueMethod, exportable);
        }

        @Override
        public String toString() {
            return reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
        }

        @Override
        public int getOrder() {
            return position;
        }
    }

    @Override
    public void generate(HSSFWorkbook hssfWorkbook, List<Exportable> exportables) {
        HSSFSheet sheet = hssfWorkbook.createSheet(sheetName());
        HSSFRow header = sheet.createRow(headerRowIndex());

        HSSFCellStyle headerStyle = headerStyle(hssfWorkbook, headerFont(hssfWorkbook));
        List<CellValueMetadata> metadataListToBeUsed = deduceMetadataListToBeUsed(exportables);
        for (CellValueMetadata metadata1 : metadataListToBeUsed) {
            HSSFCell cell = createHeaderCell(header, metadata1);
            styleHeaderCell(headerStyle, cell);
            setHeaderCellValue(metadata1, cell);
        }

        int rowNum = rowStartIndex();
        for (Exportable exportable : exportables) {
            HSSFRow row = sheet.createRow(rowNum++);
            for (CellValueMetadata metadata : metadataListToBeUsed) {
                setRowCellValue(metadata, exportable, createRowCell(row, metadata));
            }
        }

        for (CellValueMetadata metadata : metadataListToBeUsed) {
            doWithSheetAndCellValueMetadata(sheet, metadata);
        }
    }

    /**
     * Row index on the worksheet from which data rows start.
     *
     * <p>Default row start index is {@code 1}
     * Implementations can override to provide a non-default
     * row start index.</p>
     *
     * @return int data row index
     */
    protected int rowStartIndex() {
        return 1;
    }

    /**
     * Header row index on the worksheet.
     *
     * <p>Default header row index is {@code 0}
     * Implementations can override to provide a non-default
     * header row index.</p>
     *
     * @return int data row index
     */
    protected int headerRowIndex() {
        return 0;
    }

    /**
     * Sheet level operations
     *
     * <ol>
     *     <li>Column hiding based on metadata</li>
     *     <li>Column resizing based on metadata</li>
     * </ol>
     *
     * @param sheet HSSFSheet
     * @param metadata CellValueMetadata
     */
    protected void doWithSheetAndCellValueMetadata(HSSFSheet sheet, CellValueMetadata metadata) {
        sheet.setColumnHidden(metadata.position, !metadata.visibility);
        sheet.autoSizeColumn(metadata.position);
    }

    /**
     * Populate value into the workbook cell using the row exportable and
     * cell value metadata
     *
     * @param metadata CellValueMetadata
     * @param rowData Exportable row object
     * @param rowCell HSSFCell Workbook row cell
     */
    protected void setRowCellValue(CellValueMetadata metadata, Exportable rowData, HSSFCell rowCell) {
        rowCell.setCellValue(metadata.valueFrom(rowData));
    }

    /**
     * Invoked by
     * {@link AnnotatedParsingExcelBuilder#generate(org.apache.poi.hssf.usermodel.HSSFWorkbook, java.util.List)}
     *
     * to build a new cell in the worksheet row using the
     * supplied {@link CellValueMetadata}
     *
     * @param row HSSFRow
     * @param metadata CellValueMetadata
     * @return HSSFCell
     */
    protected HSSFCell createRowCell(HSSFRow row, CellValueMetadata metadata) {
        return createRowCellAtLocationSpecifiedByMetadata(row, metadata);
    }

    private HSSFCell createRowCellAtLocationSpecifiedByMetadata(HSSFRow row, CellValueMetadata metadata) {
        return row.createCell(metadata.position);
    }

    /**
     * Populate header value into the workbook cell using metadata.
     *
     * @param metadata CellValueMetadata
     * @param cell HSSFCell
     */
    protected void setHeaderCellValue(CellValueMetadata metadata, HSSFCell cell) {
        cell.setCellValue(metadata.headerText);
    }

    protected void styleHeaderCell(HSSFCellStyle headerStyle, HSSFCell cell) {
        cell.setCellStyle(headerStyle);
    }

    protected HSSFCell createHeaderCell(HSSFRow header, CellValueMetadata metadata) {
        return createRowCellAtLocationSpecifiedByMetadata(header, metadata);
    }

    /**
     * Build header cell style information.
     *
     * @param hssfWorkbook HSSFWorkbook
     * @param font HSSFFont
     * @return HSSFCellStyle
     */
    protected HSSFCellStyle headerStyle(HSSFWorkbook hssfWorkbook, HSSFFont font) {
        HSSFCellStyle headerStyle = hssfWorkbook.createCellStyle();
        headerStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setFont(font);
        return headerStyle;
    }

    /**
     * Build header cell font to be used in
     * {@link AnnotatedParsingExcelBuilder#headerStyle(org.apache.poi.hssf.usermodel.HSSFWorkbook, org.apache.poi.hssf.usermodel.HSSFFont)}
     *
     * @param hssfWorkbook HSSFWorkbook
     * @return HSSFFont
     */
    protected HSSFFont headerFont(HSSFWorkbook hssfWorkbook) {
        HSSFFont font = hssfWorkbook.createFont();
        font.setBoldweight((short) 700);
        return font;
    }

    protected List<CellValueMetadata> deduceMetadataListToBeUsed(List<Exportable> exportables) {
        return masterMetadataList;
    }

    public Class<Exportable> exportableClass() {
        return domainClass;
    }

    protected abstract String sheetName();
}