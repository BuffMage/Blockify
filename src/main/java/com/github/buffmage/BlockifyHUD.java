package com.github.buffmage;

import com.github.buffmage.util.RenderUtil;
import com.github.buffmage.util.SpotifyUtil;
import com.github.buffmage.util.URLImage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.List;

public class BlockifyHUD
{
    private static MinecraftClient client;
    private static MatrixStack matrixStack;
    private static int scaledWidth;
    private static int scaledHeight;
    private static URLImage albumImage;
    private static TextRenderer fontRenderer;
    private static String[] hudInfo;
    private static int ticks;
    private static String prevImage;
    private static int progressMS;
    private static int durationMS;
    public static boolean isHidden = false;

    public BlockifyHUD(MinecraftClient client)
    {
        this.client = client;
        scaledWidth = client.getWindow().getScaledWidth();
        scaledHeight = client.getWindow().getScaledHeight();
        albumImage = new URLImage(300, 300);
        fontRenderer = client.textRenderer;
        ticks = 0;
        hudInfo = new String[5];
        prevImage = "empty";
        progressMS = 0;
        durationMS = -1;
    }

    public static void draw(MatrixStack matrixStack)
    {
        if (hudInfo[1] == null || isHidden)
        {
            return;
        }

        double percentProgress = (double) progressMS / (double) durationMS;
        if (percentProgress < 0)
        {
            percentProgress = 0;
        }

        BlockifyHUD.matrixStack = matrixStack;
        scaledWidth = client.getWindow().getScaledWidth();
        scaledHeight = client.getWindow().getScaledHeight();
        drawRectangle(0, 0, 185, 55, new Color(0, 0, 0, 100));
        drawRectangle(60, 48, 180, 50, new Color(100, 100, 100, 255));
        drawRectangle(60, 48, (float) (60 + (120 * percentProgress)), 50, new Color(230, 230, 230, 255));

        if (hudInfo[4] != null && (!prevImage.equals(hudInfo[4]) && !hudInfo[4].equals("")))
        {
            System.out.println("Drawing new album cover.");
            albumImage.setImage(hudInfo[4]);
            prevImage = hudInfo[4];
        }


        drawRectangle(5, 5, 50, 50, new Color(0,0,0,150));
        if (hudInfo[4] != null)
        {
            RenderUtil.drawTexture(matrixStack, albumImage, 5, 5, .15F);
        }


        List<OrderedText> nameWrap = fontRenderer.wrapLines(StringVisitable.plain(hudInfo[0]), 125);
        int yOffset = 0;
        if (nameWrap.size() > 1)
        {
            fontRenderer.drawWithShadow(matrixStack, nameWrap.get(0), 60, 10, new Color(255, 255, 255, 255).getRGB());
            fontRenderer.drawWithShadow(matrixStack, nameWrap.get(1), 60, 23, new Color(255, 255, 255, 255).getRGB());
            yOffset = 25;
        }
        else
        {
            fontRenderer.drawWithShadow(matrixStack, nameWrap.get(0), 60, 10, new Color(255, 255, 255, 255).getRGB());
            yOffset = 0;
        }
        matrixStack.scale(.5F, .5F, .5F);
        fontRenderer.drawWithShadow(matrixStack, hudInfo[1], 120, 45 + yOffset, new Color(255, 255, 255, 255).getRGB());
        String progressText = (progressMS / (1000 * 60)) + ":" + String.format("%02d", (progressMS / 1000 % 60));
        String durationText = (durationMS / (1000 * 60)) + ":" + String.format("%02d", (durationMS / 1000 % 60));

        fontRenderer.drawWithShadow(matrixStack, progressText, 120, 85, new Color(255, 255, 255, 255).getRGB());
        fontRenderer.drawWithShadow(matrixStack, durationText, 360 - (fontRenderer.getWidth(durationText)), 85, new Color(255, 255, 255, 255).getRGB());
        matrixStack.scale(2F, 2F, 2F);
    }

    public static void drawRectangle(float x1, float y1, float x2, float y2, Color color)
    {
        InGameHud.fill(matrixStack, (int) (x1), (int) (y1), (int) (x2), (int) (y2), color.getRGB());
    }

    public static void updateData(String[] data)
    {
        hudInfo = data;
        progressMS = hudInfo[2] == null ? 0 : (Integer.parseInt(hudInfo[2]) - 1000);
        durationMS = hudInfo[3] == null ? -1 : Integer.parseInt(hudInfo[3]);
    }

    public static int getProgress()
    {
        return progressMS;
    }

    public static int getDuration()
    {
        return durationMS;
    }

    public static void setProgress(int progress)
    {
        progressMS = progress;
    }

    public static void setDuration(int duration)
    {
        durationMS = duration;
    }



}