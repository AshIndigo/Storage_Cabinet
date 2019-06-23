package com.ashindigo.storagecabinet;

import net.minecraftforge.common.ForgeConfigSpec;

//@Config(modid = StorageCabinetMod.MODID)
public class StorageCabinetConfig  {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec   spec = BUILDER.build();
    public static final StorageCabinetConfig INSTANCE = new StorageCabinetConfig(BUILDER);


    //    @Config.Comment("Makes it so that cabinets can only take items that can't stack I.E bows or swords and not cobblestone")
//    @Config.Name("Only Allow Non Stackables")
//    @Config.RequiresMcRestart

    public ForgeConfigSpec.ConfigValue<Boolean> onlyNonStackables;
    public boolean nonStackablesOnly;

//    @Config.Comment("Enables the blacklist/whitelist function")
//    @Config.Name("Enable Blacklist/Whitelist")
//    @Config.RequiresMcRestart
    public static boolean listEnabled = false;

//    @Config.Comment("True for blacklist, false for whitelist, see the wiki for instructions")
//    @Config.Name("Toggle white or black list")
//    @Config.RequiresMcRestart
    public static boolean blackorwhitelist = true;

    StorageCabinetConfig(ForgeConfigSpec.Builder builder) {
        //spec = BUILDER.build();
        builder.push("General");
        onlyNonStackables = builder.comment("Makes it so that cabinets can only take items that can't stack I.E bows or swords and not cobblestone").translation("config.storagecabinet.onlynonstackables").define("onlyNonStackables", true);
        builder.build();
        builder.pop();
    }
}
