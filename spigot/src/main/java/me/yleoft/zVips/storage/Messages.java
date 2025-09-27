package me.yleoft.zVips.storage;

import me.yleoft.zVips.zVipsBukkit;
import org.jetbrains.annotations.Nullable;

import static me.yleoft.zAPI.utils.ConfigUtils.formPath;

public class Messages {

    public static String hooks = "hooks";
    public static String cmds = "commands";
    public static String vault = "vault";

    public interface DefaultEnum {
        default String defparse(@Nullable String result, String... replacements) {
            if(result == null) result = this.toString();
            for (int i = 0; i < replacements.length; i += 2) {
                String placeholder = replacements[i];
                String value = (i + 1 < replacements.length) ? replacements[i + 1] : "";
                result = result.replace(placeholder, value);
            }
            return result;
        }
        default String defparse(String... replacements) {
            return defparse(null, replacements);
        }

        static String defpath() {
            return formPath();
        }
    }
    public interface CommandsEnum extends DefaultEnum {
        default String parse(String... replacements) {
            return defparse(replacements);
        }

        static String defpath() {
            return formPath(DefaultEnum.defpath(), cmds);
        }
    }





    public enum MainCMD implements CommandsEnum {
        ;

        public static String path() {
            return formPath(CommandsEnum.defpath(), "main");
        }

        private static String parseCMD(String text) {
            return text.replace("%command%", zVipsBukkit.cfgu.CmdMainCommand());
        }

        public static enum Help implements CommandsEnum {
            HELP_NOPERM("help-noperm"),
            HELP_PERM("help-perm"),
            ;

            private final String path;
            Help(String path) {
                this.path = path;
            }

            public String path() {
                return formPath(MainCMD.path(), "help");
            }

            @Override
            public String toString() {
                return parseCMD(zVipsBukkit.langm.getString(formPath(path(), path)));
            }
        }

    }

}
