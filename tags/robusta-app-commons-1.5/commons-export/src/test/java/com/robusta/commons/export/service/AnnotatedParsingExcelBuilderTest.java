package com.robusta.commons.export.service;

import com.robusta.commons.export.api.CellValue;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AnnotatedParsingExcelBuilderTest {
    private AnnotatedParsingExcelBuilder<Data> builder;

    @Before
    public void setUp() throws Exception {
        builder = new AnnotatedParsingExcelBuilder<Data>(Data.class) {
            @Override
            protected String sheetName() {
                return "test";
            }
        };
    }

    @Test
    public void testGenerate() throws Exception {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        builder.generate(hssfWorkbook, buildAListOfData(10));
        HSSFSheet sheet = hssfWorkbook.getSheet("test");
        assertNotNull(sheet);
        assertThat(sheet.getRow(0).getCell(0).getStringCellValue(), is("Header"));
        for(int i = 1; i < 10; i++) {
            assertThat(sheet.getRow(i).getCell(0).getStringCellValue(), is("Index of " + i));
        }
    }

    private List<Data> buildAListOfData(int count) {
        List<Data> list = newArrayList();
        for(int i = 1; i < count; i++) {
            list.add(new DataImpl(i));
        }
        return list;
    }

    interface Data {
        @CellValue(position = 0, headerText = "Header")
        public String id();
    }

    private class DataImpl implements Data {
        private final int index;

        public DataImpl(int index) {
            this.index = index;
        }

        @Override
        public String id() {
            return "Index of " + index;
        }
    }
}
