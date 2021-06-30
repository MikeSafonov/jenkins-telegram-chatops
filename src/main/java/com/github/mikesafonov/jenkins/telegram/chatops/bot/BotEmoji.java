package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import static com.vdurmont.emoji.EmojiManager.getForAlias;

/**
 * @author Mike Safonov
 */
@UtilityClass
public class BotEmoji {
    public static final String FOLDER_UNICODE = getForAlias("file_folder").getUnicode();
    public static final String RUNNABLE_UNICODE = getForAlias("rocket").getUnicode();
    public static final String BACK_UNICODE = getForAlias("arrow_backward").getUnicode();


    public static String replaceLeadingEmoji(String text, String emoji) {
        var withoutEmoji = text.replace(emoji, "");
        return StringUtils.trimLeadingWhitespace(withoutEmoji);
    }
}
