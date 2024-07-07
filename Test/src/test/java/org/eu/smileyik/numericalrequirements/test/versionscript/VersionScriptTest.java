package org.eu.smileyik.numericalrequirements.test.versionscript;

import org.bukkit.Bukkit;
import org.eu.smileyik.numericalrequirements.test.NeedTest;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

@NeedTest
public class VersionScriptTest {
    @NeedTest
    public void testVersion1Script() {
        VersionScript.VERSION = "v1_8_R1";
        System.out.println("Version: " + VersionScript.VERSION);
        String ret = VersionScript.runScript(new String[] {
                "$1 >= 21; /reflect-class/NMSItemStack_1_21.txt",
                "$1 >= 20; /reflect-class/NMSItemStack_1_20.txt",
                "$1 >= 19; /reflect-class/NMSItemStack_1_19.txt",
                "$1 >= 18; $2 = R1; /reflect-class/NMSItemStack_1_18.txt",
                "$1 >= 18; /reflect-class/NMSItemStack_1_18_2.txt",
                "$1 >= 17; /reflect-class/NMSItemStack_1_17.txt",
                "$1 >= 5; /reflect-class/NMSItemStack_1_5_to_1_16.txt"
        });
        System.out.println(ret);
        assert ret != null && ret.equals("/reflect-class/NMSItemStack_1_5_to_1_16.txt") : "ret is not /reflect-class/NMSItemStack_1_5_to_1_16.txt";
    }

    @NeedTest
    public void testVersion2Script() {
        VersionScript.VERSION = "v1_18_R1";
        System.out.println("Version: " + VersionScript.VERSION);
        String ret = VersionScript.runScript(new String[] {
                "$1 >= 21; /reflect-class/NMSItemStack_1_21.txt",
                "$1 >= 20; /reflect-class/NMSItemStack_1_20.txt",
                "$1 >= 19; /reflect-class/NMSItemStack_1_19.txt",
                "$1 >= 18; $2 = R1; /reflect-class/NMSItemStack_1_18.txt",
                "$1 >= 18; /reflect-class/NMSItemStack_1_18_2.txt",
                "$1 >= 17; /reflect-class/NMSItemStack_1_17.txt",
                "$1 >= 5; /reflect-class/NMSItemStack_1_5_to_1_16.txt"
        });
        System.out.println(ret);
        assert ret != null && ret.equals("/reflect-class/NMSItemStack_1_18.txt") : "ret is not /reflect-class/NMSItemStack_1_18.txt";
    }

    @NeedTest
    public void testVersion3Script() {
        VersionScript.VERSION = "v1_18_R4";
        System.out.println("Version: " + VersionScript.VERSION);
        String ret = VersionScript.runScript(new String[] {
                "$1 >= 21; /reflect-class/NMSItemStack_1_21.txt",
                "$1 >= 20; /reflect-class/NMSItemStack_1_20.txt",
                "$1 >= 19; /reflect-class/NMSItemStack_1_19.txt",
                "$1 >= 18; $2 = R1; /reflect-class/NMSItemStack_1_18.txt",
                "$1 >= 18; /reflect-class/NMSItemStack_1_18_2.txt",
                "$1 >= 17; /reflect-class/NMSItemStack_1_17.txt",
                "$1 >= 5; /reflect-class/NMSItemStack_1_5_to_1_16.txt"
        });
        System.out.println(ret);
        assert ret != null && ret.equals("/reflect-class/NMSItemStack_1_18_2.txt") : "ret is not /reflect-class/NMSItemStack_1_18_2.txt";
    }

    @NeedTest
    public void testVersion4Script() throws IOException {
        VersionScript.VERSION = "v1_18_R4";
        System.out.println("Version: " + VersionScript.VERSION);
        String ret = VersionScript.runScriptByResource("/version-script/NMSItemStack.txt");
        System.out.println(ret);
        assert ret != null && ret.equals("/reflect-class/NMSItemStack_1_18_2.txt") : "ret is not /reflect-class/NMSItemStack_1_18_2.txt";
    }

    @NeedTest
    public void testCurrentVersion1Script() throws IOException {
        VersionScript.VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        System.out.println("Version: " + VersionScript.VERSION);
        String ret = VersionScript.runScriptByResource("/version-script/NMSItemStack.txt");
        System.out.println(ret);
    }

    @NeedTest
    public void testCurrentVersion2Script() throws IOException {
        VersionScript.VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        System.out.println("Version: " + VersionScript.VERSION);
        String ret = VersionScript.runScriptByResource("/version-script/NBTTagCompound.txt");
        System.out.println(ret);
    }
}
