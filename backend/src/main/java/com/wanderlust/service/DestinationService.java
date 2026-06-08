package com.wanderlust.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanderlust.entity.Destination;
import com.wanderlust.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private final ZhipuService zhipuService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Destination getById(Long id) {
        return destinationRepository.findById(id).orElseThrow(() -> new RuntimeException("目的地不存在"));
    }

    /**
     * 🔥 首页动态推荐：获取当前月份最适合去的地点
     */
    public List<Destination> getRecommendedDestinations() {
        // 1. 获取当前系统月份 (1-12)
        int currentMonth = LocalDate.now().getMonthValue();
        log.info("🏠 首页正在为用户生成 {} 月份的旅行推荐...", currentMonth);

        // 2. 查询当前月份的景点
        List<Destination> list = destinationRepository.findByBestMonthOrderByRatingDesc(currentMonth);

        // 3. 防御性逻辑：如果当前月份没数据，随机抓 4 条展示，防止页面空白
        if (list.isEmpty()) {
            log.warn("⚠️ 数据库中没有 {} 月的最佳旅行地，将展示默认精选", currentMonth);
            return destinationRepository.findAll(PageRequest.of(0, 4)).getContent();
        }

        // 返回前 4 条最热门的
        return list.size() > 4 ? list.subList(0, 4) : list;
    }

    /**
     * 🔥 Excel 导入与向量化
     */
    public void importDestinations(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String title = getCellValue(row.getCell(0));
                String country = getCellValue(row.getCell(1));
                String monthStr = getCellValue(row.getCell(2));
                String description = getCellValue(row.getCell(3));
                String posterUrl = getCellValue(row.getCell(4));

                if (title.isEmpty()) continue;

                Destination dest = new Destination();
                dest.setTitle(title);
                dest.setCountry(country);
                dest.setDescription(description);
                dest.setPosterUrl(posterUrl);
                // 评分：9.0 + 1.0以内的随机数，保留一位小数
                dest.setRating(Math.round((9.0 + Math.random()) * 10) / 10.0);

                try {
                    // 处理 Excel 中可能的数字格式 (如 11.0)
                    dest.setBestMonth((int) Double.parseDouble(monthStr));
                } catch (Exception e) {
                    dest.setBestMonth(null);
                }

                // 向量化逻辑
                StringBuilder textToEmbed = new StringBuilder();
                textToEmbed.append("目的地：").append(title).append("。");
                textToEmbed.append("国家地区：").append(country).append("。");
                if (dest.getBestMonth() != null) {
                    textToEmbed.append("最佳旅行月份：").append(dest.getBestMonth()).append("月。");
                }
                textToEmbed.append("景观氛围与体验：").append(description);

                log.info("⚡ 生成向量 [{}]...", title);
                List<Double> vector = zhipuService.getEmbedding(textToEmbed.toString());

                if (vector != null && !vector.isEmpty()) {
                    dest.setEmbeddingVector(objectMapper.writeValueAsString(vector));
                }

                destinationRepository.save(dest);
            }
        } catch (Exception e) {
            log.error("导入过程中发生错误", e);
            throw new RuntimeException("导入失败: " + e.getMessage());
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        return cell.toString().trim();
    }
}