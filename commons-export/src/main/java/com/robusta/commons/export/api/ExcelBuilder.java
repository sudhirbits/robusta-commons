package com.robusta.commons.export.api;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

public interface ExcelBuilder<Exportable> {
    /**
     * Build a worksheet in the workbook with the list of exportable
     * objects.
     *
     * <p>Each element of the exportable list will be used to
     * populate a row of the worksheet.</p>
     *
     * <p>Implementations are expected to create the worksheet
     * and populate a header row.</p>
     *
     * @param hssfWorkbook HSSFWorkbook
     * @param exportables List of exportables
     */
    void generate(HSSFWorkbook hssfWorkbook, List<Exportable> exportables);
}
