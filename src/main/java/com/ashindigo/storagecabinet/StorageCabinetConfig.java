package com.ashindigo.storagecabinet;

import net.minecraftforge.common.config.Config;

@Config(modid = StorageCabinetMod.MODID)
public class StorageCabinetConfig {

    @Config.Comment("Makes it so that cabinets can only take items that can't stack I.E bows or swords and not cobblestone")
    @Config.Name("Only Allow Non Stackables")
    @Config.RequiresMcRestart
    public static boolean onlyNonStackables = false;


}
