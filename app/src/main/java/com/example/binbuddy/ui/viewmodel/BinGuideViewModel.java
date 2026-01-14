package com.example.binbuddy.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.binbuddy.domain.model.WasteCategory;

import java.util.ArrayList;
import java.util.List;

public class BinGuideViewModel extends AndroidViewModel {
    private final MutableLiveData<List<WasteCategory>> wasteCategories = new MutableLiveData<>();
    private final MutableLiveData<WasteCategory> selectedCategory = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public BinGuideViewModel(@NonNull Application application) {
        super(application);
        loadCategories();
    }

    public LiveData<List<WasteCategory>> getWasteCategories() {
        return wasteCategories;
    }

    public LiveData<WasteCategory> getSelectedCategory() {
        return selectedCategory;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadCategories() {
        isLoading.setValue(true);
        error.setValue(null);

        // TODO: Load from repository when Hilt is set up
        // For now, create sample categories
        List<WasteCategory> categories = new ArrayList<>();
        categories.add(new WasteCategory.Builder()
                .setId("gelbe_tonne")
                .setNameDe("Gelbe Tonne")
                .setNameEn("Yellow Bin")
                .setDescriptionDe("Verpackungen aus Kunststoff, Metall und Verbundmaterialien. Beispiele: Plastikflaschen, Dosen, Joghurtbecher, Tetra Paks")
                .setDescriptionEn("Packaging made of plastic, metal and composite materials. Examples: Plastic bottles, cans, yogurt cups, Tetra Paks")
                .setIconName("ic_recycle")
                .setColorHex("#FFD700")
                .build());
        categories.add(new WasteCategory.Builder()
                .setId("papier")
                .setNameDe("Papier")
                .setNameEn("Paper")
                .setDescriptionDe("Papier, Pappe, Kartonagen. Beispiele: Zeitungen, Zeitschriften, Kartons, Briefumschläge")
                .setDescriptionEn("Paper, cardboard, cartons. Examples: Newspapers, magazines, boxes, envelopes")
                .setIconName("ic_description")
                .setColorHex("#2196F3")
                .build());
        categories.add(new WasteCategory.Builder()
                .setId("glas")
                .setNameDe("Glas")
                .setNameEn("Glass")
                .setDescriptionDe("Glasflaschen und -behälter (nach Farben getrennt: Weißglas, Braunglas, Grünglas). Beispiele: Weinflaschen, Marmeladengläser, Bierflaschen")
                .setDescriptionEn("Glass bottles and containers (separated by color: Clear glass, brown glass, green glass). Examples: Wine bottles, jam jars, beer bottles")
                .setIconName("ic_invert_colors")
                .setColorHex("#4CAF50")
                .build());
        categories.add(new WasteCategory.Builder()
                .setId("bio")
                .setNameDe("Bio")
                .setNameEn("Organic")
                .setDescriptionDe("Organische Abfälle, Lebensmittelreste. Beispiele: Obst- und Gemüsereste, Kaffeesatz, Eierschalen, Gartenabfälle")
                .setDescriptionEn("Organic waste, food scraps. Examples: Fruit and vegetable scraps, coffee grounds, eggshells, garden waste")
                .setIconName("ic_eco")
                .setColorHex("#8BC34A")
                .build());
        categories.add(new WasteCategory.Builder()
                .setId("restmuell")
                .setNameDe("Restmüll")
                .setNameEn("Residual Waste")
                .setDescriptionDe("Nicht recycelbare Abfälle. Beispiele: Asche, Staubsaugerbeutel, Hygieneartikel, verschmutzte Verpackungen")
                .setDescriptionEn("Non-recyclable waste. Examples: Ash, vacuum cleaner bags, hygiene products, dirty packaging")
                .setIconName("ic_delete")
                .setColorHex("#757575")
                .build());
        categories.add(new WasteCategory.Builder()
                .setId("pfand")
                .setNameDe("Pfand")
                .setNameEn("Deposit")
                .setDescriptionDe("Pfandpflichtige Flaschen und Dosen. Beispiele: Bierflaschen, Softdrink-Flaschen, Dosen mit Pfandzeichen")
                .setDescriptionEn("Deposit bottles and cans. Examples: Beer bottles, soft drink bottles, cans with deposit symbol")
                .setIconName("ic_attach_money")
                .setColorHex("#FF9800")
                .build());

        wasteCategories.setValue(categories);
        isLoading.setValue(false);
    }

    public void selectCategory(String categoryId) {
        List<WasteCategory> categories = wasteCategories.getValue();
        if (categories != null) {
            for (WasteCategory category : categories) {
                if (category.getId().equals(categoryId)) {
                    selectedCategory.setValue(category);
                    return;
                }
            }
        }
    }
}
