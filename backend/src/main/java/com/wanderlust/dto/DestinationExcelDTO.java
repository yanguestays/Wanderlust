package com.wanderlust.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DestinationExcelDTO {

    // 🔥🔥 修改 1: 必须匹配 Excel 的 "地名 (Title)"
    @ExcelProperty("地名 (Title)")
    private String title;

    // 🔥🔥 修改 2: 必须匹配 Excel 的 "国家 (Country)"
    @ExcelProperty("国家 (Country)")
    private String country;

    // 🔥🔥 修改 3: 必须匹配 Excel 的 "最佳月份 (Month)"
    @ExcelProperty("最佳月份 (Month)")
    private Integer bestMonth;

    // 🔥🔥 修改 4: 你的截图 D 列被遮住了，但我猜是 "描述 (Description)"
    // 如果不是，请改成你 Excel 里实际的样子！
    @ExcelProperty("描述 (Description)")
    private String description;

    // 🔥🔥 修改 5: 你的截图 E 列看起来是 "URL (PosterUrl)"
    @ExcelProperty("图片URL (PosterUrl)")
    private String posterUrl;
}