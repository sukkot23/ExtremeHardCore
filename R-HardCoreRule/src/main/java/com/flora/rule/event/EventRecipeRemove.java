package com.flora.rule.event;

import com.flora.rule.M_Rule;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

public class EventRecipeRemove
{
    public EventRecipeRemove()
    {
        Iterator iterator = JavaPlugin.getPlugin(M_Rule.class).getServer().recipeIterator();

        while (iterator.hasNext())
        {
            Recipe recipe = (Recipe) iterator.next();
            if (recipe == null) return;

            switch (recipe.getResult().getType()) {
                case WHITE_BED:
                case ORANGE_BED:
                case MAGENTA_BED:
                case LIGHT_BLUE_BED:
                case YELLOW_BED:
                case LIME_BED:
                case PINK_BED:
                case GRAY_BED:
                case LIGHT_GRAY_BED:
                case CYAN_BED:
                case PURPLE_BED:
                case BLUE_BED:
                case BROWN_BED:
                case GREEN_BED:
                case RED_BED:
                case BLACK_BED:

                case DIAMOND_HELMET:
                case DIAMOND_CHESTPLATE:
                case DIAMOND_LEGGINGS:
                case DIAMOND_BOOTS:
                    iterator.remove();
            }
        }
    }
}
