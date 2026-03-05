package com.validation.post.config;

import com.validation.post.entity.Category;
import com.validation.post.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Seeds the database with default categories on startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CategorySeeder {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    @Transactional
    public void seedCategories() {
        if (categoryRepository.count() > 0) {
            log.info("Categories already exist, skipping seed");
            return;
        }

        log.info("Seeding default categories...");
        List<Category> subCategories = new ArrayList<>();
        int order = 1;

        order = seedParentWithSubs(subCategories, order,
                "Software & Technology", "software-technology", "Software and technology startups", "\uD83D\uDCBB",
                List.of("B2B SaaS", "B2C Apps", "Developer Tools", "AI/ML Products"));

        order = seedParentWithSubs(subCategories, order,
                "Health & Wellness", "health-wellness", "Health and wellness startups", "\uD83E\uDDEC",
                List.of("Healthcare Tech", "Mental Health", "Fitness & Nutrition"));

        order = seedParentWithSubs(subCategories, order,
                "E-commerce & Retail", "ecommerce-retail", "E-commerce and retail startups", "\uD83D\uDECD\uFE0F",
                List.of("DTC Brands", "Marketplace Platforms", "Food & Beverage"));

        order = seedParentWithSubs(subCategories, order,
                "Financial Services", "financial-services", "Financial services startups", "\uD83D\uDCB0",
                List.of("Fintech Apps", "Investing & Trading", "Lending & Credit"));

        order = seedParentWithSubs(subCategories, order,
                "Education & Learning", "education-learning", "Education and learning startups", "\uD83D\uDCDA",
                List.of("Online Courses", "K-12 Education", "Corporate Training"));

        order = seedParentWithSubs(subCategories, order,
                "Real Estate & Housing", "real-estate-housing", "Real estate and housing startups", "\uD83C\uDFE1",
                List.of("PropTech", "Home Services", "Smart Home"));

        order = seedParentWithSubs(subCategories, order,
                "Transportation & Logistics", "transportation-logistics", "Transportation and logistics startups", "\uD83D\uDE97",
                List.of("Rideshare & Mobility", "Delivery & Logistics"));

        order = seedParentWithSubs(subCategories, order,
                "Sustainability & Climate", "sustainability-climate", "Sustainability and climate startups", "\uD83C\uDF31",
                List.of("Clean Energy", "Sustainable Products"));

        order = seedParentWithSubs(subCategories, order,
                "Media & Entertainment", "media-entertainment", "Media and entertainment startups", "\uD83C\uDFA8",
                List.of("Content Platforms", "Creator Tools"));

        order = seedParentWithSubs(subCategories, order,
                "Business Services", "business-services", "Business services startups", "\uD83C\uDFE2",
                List.of("HR & Recruiting", "Sales & Marketing"));

        order = seedParentWithSubs(subCategories, order,
                "Food & Hospitality", "food-hospitality", "Food and hospitality startups", "\uD83C\uDF55",
                List.of("Restaurant Tech", "Cloud Kitchens"));

        order = seedParentWithSubs(subCategories, order,
                "Professional & Career", "professional-career", "Professional and career startups", "\uD83C\uDF93",
                List.of("Job Boards", "Freelance & Gig"));

        order = seedParentWithSubs(subCategories, order,
                "Family & Parenting", "family-parenting", "Family and parenting startups", "\uD83D\uDC76",
                List.of("Childcare", "Parenting Tools"));

        seedParentWithSubs(subCategories, order,
                "Hardware & Manufacturing", "hardware-manufacturing", "Hardware and manufacturing startups", "\uD83D\uDD27",
                List.of("Consumer Electronics", "IoT"));

        categoryRepository.saveAll(subCategories);
        log.info("Seeded {} categories total", subCategories.size() + 14);
    }

    private int seedParentWithSubs(List<Category> pendingSubs, int order,
                                   String name, String slug, String description, String icon,
                                   List<String> subNames) {
        Category parent = categoryRepository.save(Category.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .icon(icon)
                .displayOrder(order)
                .build());

        UUID parentId = parent.getId();
        int subOrder = 1;
        for (String subName : subNames) {
            String subSlug = slug + "-" + subName.toLowerCase()
                    .replaceAll("[&/]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-+", "-");
            pendingSubs.add(Category.builder()
                    .name(subName)
                    .slug(subSlug)
                    .description(subName + " in " + name)
                    .parentCategoryId(parentId)
                    .displayOrder(subOrder++)
                    .build());
        }

        return order + 1;
    }
}
