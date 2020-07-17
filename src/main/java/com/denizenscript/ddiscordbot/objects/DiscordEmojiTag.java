package com.denizenscript.ddiscordbot.objects;

import com.denizenscript.ddiscordbot.DenizenDiscordBot;
import com.denizenscript.ddiscordbot.DiscordConnection;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagRunnable;
import discord4j.core.object.reaction.ReactionEmoji;

public class DiscordEmojiTag implements ObjectTag {

    @Fetchable("discordemoji")
    public static DiscordEmojiTag valueOf(String string, TagContext context) {
        if (string.startsWith("discordemoji@")) {
            string = string.substring("discordemoji@".length());
        }
        if (string.contains("@")) {
            return null;
        }
        String bot = string.split(",")[0];
        String type = string.split(",")[1];
        if (type.equalsIgnoreCase("unicode")) {
            String id = string.split(",")[2];
            return new DiscordEmojiTag(bot, ReactionEmoji.unicode(id));
        }
        return null;
    }

    public static boolean matches(String arg) {
        if (arg.startsWith("discordemoji@")) {
            return true;
        }
        if (arg.contains("@")) {
            return false;
        }
        int comma = arg.indexOf(',');
        if (comma == -1) {
            return ArgumentHelper.matchesInteger(arg);
        }
        if (comma == arg.length() - 1) {
            return false;
        }
        return ArgumentHelper.matchesInteger(arg.substring(comma + 1));
    }

    public DiscordEmojiTag(String bot, ReactionEmoji emoji) {
        this.bot = bot;
        this.emoji = emoji;
    }

    public DiscordConnection getBot() {
        return DenizenDiscordBot.instance.connections.get(bot);
    }

    public String bot;

    public ReactionEmoji emoji;

    public String emoji_id;

    public static void registerTags() {

        // <--[tag]
        // @attribute <DiscordEmojiTag.id>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns the ID of the emoji.
        // -->
        registerTag("id", (attribute, object) -> {
            String emoji_id;
            if (object.emoji.asCustomEmoji().isPresent()) {
                emoji_id = object.emoji.asCustomEmoji().get().getId().asString();
            } else if (object.emoji.asUnicodeEmoji().isPresent()){
                emoji_id = object.emoji.asUnicodeEmoji().get().getRaw();
            } else {
                emoji_id = "INVALID";
            }
            return new ElementTag(emoji_id);
        });
        // <--[tag]
        // @attribute <DiscordEmojiTag.name>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns the name of the emoji.
        // -->
        registerTag("name", (attribute, object) -> {
            String emoji_id;
            if (object.emoji.asCustomEmoji().isPresent()) {
                emoji_id = object.emoji.asCustomEmoji().get().getName();
            } else if (object.emoji.asUnicodeEmoji().isPresent()){
                emoji_id = object.emoji.asUnicodeEmoji().get().getRaw();
            } else {
                emoji_id = "INVALID";
            }
            return new ElementTag(emoji_id);
        });
        // <--[tag]
        // @attribute <DiscordEmojiTag.animated>
        // @returns ElementTag
        // @plugin dDiscordBot
        // @description
        // Returns whether or not the emoji is animated.
        // -->
        registerTag("animated", (attribute, object) -> {
            boolean animated;
            if (object.emoji.asCustomEmoji().isPresent()) {
                animated = object.emoji.asCustomEmoji().get().isAnimated();
            } else {
                animated = false;
            }
            return new ElementTag(animated);
        });
    }

    public static ObjectTagProcessor<DiscordEmojiTag> tagProcessor = new ObjectTagProcessor<>();

    public static void registerTag(String name, TagRunnable.ObjectInterface<DiscordEmojiTag> runnable, String... variants) {
        tagProcessor.registerTag(name, runnable, variants);
    }

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    String prefix = "discordemoji";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String debug() {
        return (prefix + "='<A>" + identify() + "<G>'  ");
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public String getObjectType() {
        return "DiscordEmoji";
    }

    @Override
    public String identify() {
        if (emoji.asCustomEmoji().isPresent()) {
            emoji_id = "custom," + emoji.asCustomEmoji().get().getId().asString();
        } else if (emoji.asUnicodeEmoji().isPresent()){
            emoji_id = "unicode," + emoji.asUnicodeEmoji().get().getRaw();
        } else {
            emoji_id = "INVALID";
        }
        if (bot != null) {
            return "discordemoji@" + bot + "," + emoji_id;
        }
        return "discordemoji@" + emoji_id;
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        if (prefix != null) {
            this.prefix = prefix;
        }
        return this;
    }
}
