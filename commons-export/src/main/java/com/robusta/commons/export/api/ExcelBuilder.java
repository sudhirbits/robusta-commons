package com.robusta.commons.export.api;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

public interface ExcelBuilder<Exportable> {
    void generate(HSSFWorkbook hssfWorkbook, List<Exportable> exportables);
}
