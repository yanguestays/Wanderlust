package com.wanderlust.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanderlust.entity.Destination;
import com.wanderlust.repository.DestinationRepository;
import com.wanderlust.service.ZhipuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DestinationRepository destinationRepository;
    private final ZhipuService zhipuService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        // 1. 检查数据库是否为空，防止重复插入
        if (destinationRepository.count() > 0) {
            log.info("数据库已有数据，跳过初始化");
            return;
        }

        log.info("数据库为空，开始自动播种初始化数据...");

        List<Destination> seeds = new ArrayList<>();

        // --- 数据 1: 冰岛 ---
        seeds.add(createDestination(
                "冰岛·维克黑沙滩",
                "北欧",
                11,
                "世界尽头的冷酷仙境，巨大的玄武岩柱与咆哮的北大西洋海浪。适合孤独的灵魂，拍摄情绪大片。这里有着极其震撼的黑色沙滩，是《权力的游戏》取景地。",
                "https://images.unsplash.com/photo-1520699697851-3cdc68ed3d1c?q=80&w=2694&auto=format&fit=crop",
                9.8
        ));

        // --- 数据 2: 日本京都 ---
        seeds.add(createDestination(
                "京都·岚山竹林",
                "日本",
                4,
                "幽静的竹林小径，隐世的禅意庭院。春日樱花漫天，秋日红叶如火。适合寻找内心的宁静，体验古老的茶道文化。",
                "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?q=80&w=2670&auto=format&fit=crop",
                9.5
        ));

        // --- 数据 3: 重庆 ---
        seeds.add(createDestination(
                "中国·赛博重庆",
                "中国",
                10,
                "8D魔幻城市，穿楼而过的轻轨，洪崖洞的璀璨夜景仿佛穿越到了《千与千寻》的世界。适合摄影爱好者、赛博朋克迷和火锅狂魔。",
                "https://images.unsplash.com/photo-1548232979-6c557ee14752?q=80&w=2671&auto=format&fit=crop",
                9.2
        ));

        // --- 数据 4: 马尔代夫 ---
        seeds.add(createDestination(
                "马尔代夫·宁静岛",
                "南亚",
                12,
                "一价全包的奢华海岛，拥有玻璃地板的水上屋。适合蜜月情侣，躺在白沙滩上发呆，潜水看珊瑚，逃离城市的喧嚣，彻底治愈疲惫。",
                "https://images.unsplash.com/photo-1514282401047-d79a71a590e8?q=80&w=2667&auto=format&fit=crop",
                9.9
        ));

        // --- 数据 5: 瑞士 ---
        seeds.add(createDestination(
                "瑞士·格林德瓦",
                "欧洲",
                6,
                "童话般的山坡小镇，正对艾格峰北壁。满眼绿色的草甸，牛铃声声。适合徒步、滑翔伞，感受阿尔卑斯山的纯净与治愈。",
                "https://images.unsplash.com/photo-1506057213367-028a17ec52e5?q=80&w=2670&auto=format&fit=crop",
                9.6
        ));

        // 2. 保存并生成向量
        for (Destination d : seeds) {
            // 生成向量
            String text = d.getTitle() + " " + d.getCountry() + " " + d.getDescription();
            log.info("正在生成向量 [{}]...", d.getTitle());
            try {
                List<Double> vector = zhipuService.getEmbedding(text);
                if (!vector.isEmpty()) {
                    d.setEmbeddingVector(objectMapper.writeValueAsString(vector));
                    log.info("向量生成成功 [{}]", d.getTitle());
                }
            } catch (Exception e) {
                log.error("向量生成失败 [{}]: {}", d.getTitle(), e.getMessage());
            }
            destinationRepository.save(d);
        }

        log.info("初始化完成！写入 {} 条种子数据", seeds.size());
    }

    private Destination createDestination(String title, String country, int month, String desc, String img, double rating) {
        Destination d = new Destination();
        d.setTitle(title);
        d.setCountry(country);
        d.setBestMonth(month);
        d.setDescription(desc);
        d.setPosterUrl(img);
        d.setRating(rating);
        return d;
    }
}