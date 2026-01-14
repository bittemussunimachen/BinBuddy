package com.example.binbuddy.data.database;

import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.binbuddy.data.entity.WasteCategoryEntity;

import java.util.Arrays;
import java.util.List;

public class DatabaseInitializer extends RoomDatabase.Callback {
    @Override
    public void onCreate(SupportSQLiteDatabase db) {
        super.onCreate(db);
        seedWasteCategories(db);
    }

    private void seedWasteCategories(SupportSQLiteDatabase db) {
        // Insert waste categories directly using SQL
        List<WasteCategoryEntity> categories = Arrays.asList(
            createCategory("gelbe_tonne", "Gelbe Tonne", "Yellow Bin", 
                "Verpackungen aus Kunststoff, Metall und Verbundmaterialien", 
                "Packaging made of plastic, metal and composite materials",
                "ic_recycle", "#FFD700", 1),
            createCategory("papier", "Papier", "Paper",
                "Papier, Pappe, Kartonagen",
                "Paper, cardboard, cartons",
                "ic_description", "#2196F3", 2),
            createCategory("glas", "Glas", "Glass",
                "Glasflaschen und -behälter (nach Farben getrennt)",
                "Glass bottles and containers (separated by color)",
                "ic_invert_colors", "#4CAF50", 3),
            createCategory("bio", "Bio", "Organic",
                "Organische Abfälle, Lebensmittelreste",
                "Organic waste, food scraps",
                "ic_eco", "#8BC34A", 4),
            createCategory("restmuell", "Restmüll", "Residual Waste",
                "Nicht recycelbare Abfälle",
                "Non-recyclable waste",
                "ic_delete", "#757575", 5),
            createCategory("pfand", "Pfand", "Deposit",
                "Pfandpflichtige Flaschen und Dosen",
                "Deposit bottles and cans",
                "ic_attach_money", "#FF9800", 6)
        );

        for (WasteCategoryEntity category : categories) {
            db.execSQL(
                "INSERT INTO waste_categories (id, name_de, name_en, description_de, description_en, icon_name, color_hex, sort_order) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{
                    category.id,
                    category.nameDe,
                    category.nameEn,
                    category.descriptionDe,
                    category.descriptionEn,
                    category.iconName,
                    category.colorHex,
                    category.sortOrder
                }
            );
        }
        
        List<WasteCategoryEntity> categories = Arrays.asList(
            createCategory("gelbe_tonne", "Gelbe Tonne", "Yellow Bin", 
                "Verpackungen aus Kunststoff, Metall und Verbundmaterialien", 
                "Packaging made of plastic, metal and composite materials",
                "ic_recycle", "#FFD700", 1),
            createCategory("papier", "Papier", "Paper",
                "Papier, Pappe, Kartonagen",
                "Paper, cardboard, cartons",
                "ic_description", "#2196F3", 2),
            createCategory("glas", "Glas", "Glass",
                "Glasflaschen und -behälter (nach Farben getrennt)",
                "Glass bottles and containers (separated by color)",
                "ic_invert_colors", "#4CAF50", 3),
            createCategory("bio", "Bio", "Organic",
                "Organische Abfälle, Lebensmittelreste",
                "Organic waste, food scraps",
                "ic_eco", "#8BC34A", 4),
            createCategory("restmuell", "Restmüll", "Residual Waste",
                "Nicht recycelbare Abfälle",
                "Non-recyclable waste",
                "ic_delete", "#757575", 5),
            createCategory("pfand", "Pfand", "Deposit",
                "Pfandpflichtige Flaschen und Dosen",
                "Deposit bottles and cans",
                "ic_attach_money", "#FF9800", 6)
        );

        dao.insertCategories(categories);
    }

    private WasteCategoryEntity createCategory(String id, String nameDe, String nameEn,
                                                String descDe, String descEn,
                                                String iconName, String colorHex, int sortOrder) {
        WasteCategoryEntity category = new WasteCategoryEntity();
        category.id = id;
        category.nameDe = nameDe;
        category.nameEn = nameEn;
        category.descriptionDe = descDe;
        category.descriptionEn = descEn;
        category.iconName = iconName;
        category.colorHex = colorHex;
        category.sortOrder = sortOrder;
        return category;
    }
}
