package com.yuyang.template.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtiles {
    private final static String excel2003l = ".xls";
    private final static String excel2007l = ".xlsx";

    // 读取文件数据
    public static List<List<Object>> getExcelList(InputStream is, String fileName, Integer integer)
            throws Exception {
        List<List<Object>> list = new ArrayList<List<Object>>();
        Workbook workbook = null;
        // 文件验证格式
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (suffix.equals(excel2003l)) {
            workbook = new HSSFWorkbook(is);
        } else if (suffix.equals(excel2007l)) {
            workbook = new XSSFWorkbook(is);
        } else {
            // throw
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheet = workbook.getSheetAt(i);
            if (sheet == null){
                continue;
            }
            // 遍历循环sheet中全部行
            for (int j = sheet.getFirstRowNum() + integer; j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if (row == null){
                    continue;
                }
                // 循环row中的全部列
                List<Object> li = new ArrayList<Object>();
                for (int k = row.getFirstCellNum(); k <= row.getLastCellNum(); k++) {
                    cell = row.getCell(k);
                    if (cell != null) {
                        li.add(getCellValue(cell));
                    }else {
                        li.add("");
                    }
                }
                list.add(li);
            }
        }
        return list;

    }

    // 单元格数据类型格式化
    private static Object getCellValue(Cell cell) {
        Object value = null;
        DecimalFormat decimalFormat = new DecimalFormat("0");
        DecimalFormat decimalFormat2 = new DecimalFormat("0.00");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    value = decimalFormat.format(cell.getNumericCellValue());
                } else if ("m-d-yy".equals(cell.getCellStyle().getDataFormatString())) {
                    value = dateFormat.format(cell.getDateCellValue());
                } else {
                    value = decimalFormat2.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
        }
        return value;
    }

    //SampleInfoDownExt是需要自己建的一个增强类
    // 导出
//    public static XSSFWorkbook downLoadExcelModel(InputStream in, String sheetName, List<SampleInfoDownExt> list)
////            throws Exception {
////        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
////        // 创建一个新的Excel文件
////        XSSFWorkbook workbook = new XSSFWorkbook(in);
////        // 填充内容
////        if (list != null) {
////            // 创建Excel文件的工作表
////            XSSFSheet sheet = workbook.getSheet(sheetName);
////            int lastRow = sheet.getLastRowNum() + 1;
////            createFont(workbook);
////            for (int i = 0; i < list.size(); i++) {
////                Row row = null;// 创建行
////                Cell cell = null;// 创建列
////                row = sheet.createRow(lastRow + i);
////                SampleInfoDownExt s = list.get(i);
////                // 创建内容
////                cell = row.createCell(0);
////                cell.setCellValue(s.getSampleId());// 样品编号
////                cell.setCellStyle(fontStyle2);
////
////                cell = row.createCell(1);
////                cell.setCellValue(s.getProvince() + s.getCity() + s.getCounty());// 地点
////                cell.setCellStyle(fontStyle2);
////
////                cell = row.createCell(2);
////                cell.setCellValue(s.getCropSpeciesName());// 品种
////                cell.setCellStyle(fontStyle2);
////
////                cell = row.createCell(3);
////                cell.setCellValue(s.getSamplingTime());// 取样时间
////                cell.setCellStyle(fontStyle2);
////
////                cell = row.createCell(4);
////                cell.setCellValue(s.getInputTime());// 录入时间
////                cell.setCellStyle(fontStyle2);
////
////                cell = row.createCell(5);
////                cell.setCellValue(s.getPollutionRate());// 污染率
////                cell.setCellStyle(fontStyle2);
////
////                cell = row.createCell(6);
////                cell.setCellValue(s.getToxinName());// 主要毒素
////                cell.setCellStyle(fontStyle2);
////            }
////        }
////        // 设置Excel的字体样式
////        createFont(workbook);
////        return workbook;
////    }

    private static XSSFCellStyle fontStyle2;

    public static void createFont(XSSFWorkbook workbook) {
        // 内容
        fontStyle2 = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 12);// 设置字体大小
        fontStyle2.setFont(font2);
        fontStyle2.setBorderBottom(XSSFCellStyle.BORDER_THIN); // 下边框
        fontStyle2.setBorderLeft(XSSFCellStyle.BORDER_THIN);// 左边框
        fontStyle2.setBorderTop(XSSFCellStyle.BORDER_THIN);// 上边框
        fontStyle2.setBorderRight(XSSFCellStyle.BORDER_THIN);// 右边框
        fontStyle2.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 居中
    }


}

