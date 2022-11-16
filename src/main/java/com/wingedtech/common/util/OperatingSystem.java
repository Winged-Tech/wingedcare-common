package com.wingedtech.common.util;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public abstract class OperatingSystem {
    public static final OperatingSystem.Windows WINDOWS = new OperatingSystem.Windows();
    public static final OperatingSystem.MacOs MAC_OS = new OperatingSystem.MacOs();
    public static final OperatingSystem.Solaris SOLARIS = new OperatingSystem.Solaris();
    public static final OperatingSystem.Linux LINUX = new OperatingSystem.Linux();
    public static final OperatingSystem.FreeBSD FREE_BSD = new OperatingSystem.FreeBSD();
    public static final OperatingSystem.Unix UNIX = new OperatingSystem.Unix();
    private static OperatingSystem currentOs;
    private final String toStringValue = this.getName() + " " + this.getVersion() + " " + System.getProperty("os.arch");
    private final String osName = System.getProperty("os.name");
    private final String osVersion = System.getProperty("os.version");

    OperatingSystem() {
    }

    public static OperatingSystem current() {
        if (currentOs == null) {
            currentOs = forName(System.getProperty("os.name"));
        }

        return currentOs;
    }

    static void resetCurrent() {
        currentOs = null;
    }

    public static OperatingSystem forName(String os) {
        String osName = os.toLowerCase();
        if (osName.contains("windows")) {
            return WINDOWS;
        } else if (!osName.contains("mac os x") && !osName.contains("darwin") && !osName.contains("osx")) {
            if (!osName.contains("sunos") && !osName.contains("solaris")) {
                if (osName.contains("linux")) {
                    return LINUX;
                } else {
                    return (OperatingSystem)(osName.contains("freebsd") ? FREE_BSD : UNIX);
                }
            } else {
                return SOLARIS;
            }
        } else {
            return MAC_OS;
        }
    }

    public String toString() {
        return this.toStringValue;
    }

    public String getName() {
        return this.osName;
    }

    public String getVersion() {
        return this.osVersion;
    }

    public boolean isWindows() {
        return false;
    }

    public boolean isUnix() {
        return false;
    }

    public boolean isMacOsX() {
        return false;
    }

    public boolean isLinux() {
        return false;
    }

    public abstract String getNativePrefix();

    public abstract String getScriptName(String var1);

    public abstract String getExecutableName(String var1);

    public abstract String getExecutableSuffix();

    public abstract String getSharedLibraryName(String var1);

    public abstract String getSharedLibrarySuffix();

    public abstract String getStaticLibraryName(String var1);

    public abstract String getStaticLibrarySuffix();

    public abstract String getLinkLibrarySuffix();

    public abstract String getLinkLibraryName(String var1);

    public abstract String getFamilyName();

    @Nullable
    public File findInPath(String name) {
        String exeName = this.getExecutableName(name);
        if (exeName.contains(File.separator)) {
            File candidate = new File(exeName);
            return candidate.isFile() ? candidate : null;
        } else {
            Iterator var3 = this.getPath().iterator();

            File candidate;
            do {
                if (!var3.hasNext()) {
                    return null;
                }

                File dir = (File)var3.next();
                candidate = new File(dir, exeName);
            } while(!candidate.isFile());

            return candidate;
        }
    }

    public List<File> findAllInPath(String name) {
        List<File> all = new LinkedList();
        Iterator var3 = this.getPath().iterator();

        while(var3.hasNext()) {
            File dir = (File)var3.next();
            File candidate = new File(dir, name);
            if (candidate.isFile()) {
                all.add(candidate);
            }
        }

        return all;
    }

    public List<File> getPath() {
        String path = System.getenv(this.getPathVar());
        if (path == null) {
            return Collections.emptyList();
        } else {
            List<File> entries = new ArrayList();
            String[] var3 = path.split(Pattern.quote(File.pathSeparator));
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String entry = var3[var5];
                entries.add(new File(entry));
            }

            return entries;
        }
    }

    public String getPathVar() {
        return "PATH";
    }

    static class Solaris extends OperatingSystem.Unix {
        Solaris() {
        }

        public String getFamilyName() {
            return "solaris";
        }

        protected String getOsPrefix() {
            return "sunos";
        }

        protected String getArch() {
            String arch = System.getProperty("os.arch");
            return !arch.equals("i386") && !arch.equals("x86") ? super.getArch() : "x86";
        }
    }

    static class FreeBSD extends OperatingSystem.Unix {
        FreeBSD() {
        }
    }

    static class Linux extends OperatingSystem.Unix {
        Linux() {
        }

        public boolean isLinux() {
            return true;
        }

        public String getFamilyName() {
            return "linux";
        }
    }

    static class MacOs extends OperatingSystem.Unix {
        MacOs() {
        }

        public boolean isMacOsX() {
            return true;
        }

        public String getFamilyName() {
            return "os x";
        }

        public String getSharedLibrarySuffix() {
            return ".dylib";
        }

        public String getNativePrefix() {
            return "darwin";
        }
    }

    static class Unix extends OperatingSystem {
        private final String nativePrefix = this.resolveNativePrefix();

        Unix() {
        }

        public String getScriptName(String scriptPath) {
            return scriptPath;
        }

        public String getFamilyName() {
            return "unknown";
        }

        public String getExecutableSuffix() {
            return "";
        }

        public String getExecutableName(String executablePath) {
            return executablePath;
        }

        public String getSharedLibraryName(String libraryName) {
            return this.getLibraryName(libraryName, this.getSharedLibrarySuffix());
        }

        private String getLibraryName(String libraryName, String suffix) {
            if (libraryName.endsWith(suffix)) {
                return libraryName;
            } else {
                int pos = libraryName.lastIndexOf(47);
                return pos >= 0 ? libraryName.substring(0, pos + 1) + "lib" + libraryName.substring(pos + 1) + suffix : "lib" + libraryName + suffix;
            }
        }

        public String getSharedLibrarySuffix() {
            return ".so";
        }

        public String getLinkLibrarySuffix() {
            return this.getSharedLibrarySuffix();
        }

        public String getLinkLibraryName(String libraryPath) {
            return this.getSharedLibraryName(libraryPath);
        }

        public String getStaticLibrarySuffix() {
            return ".a";
        }

        public String getStaticLibraryName(String libraryName) {
            return this.getLibraryName(libraryName, ".a");
        }

        public boolean isUnix() {
            return true;
        }

        public String getNativePrefix() {
            return this.nativePrefix;
        }

        private String resolveNativePrefix() {
            String arch = this.getArch();
            String osPrefix = this.getOsPrefix();
            osPrefix = osPrefix + "-" + arch;
            return osPrefix;
        }

        protected String getArch() {
            String arch = System.getProperty("os.arch");
            if ("x86".equals(arch)) {
                arch = "i386";
            }

            if ("x86_64".equals(arch)) {
                arch = "amd64";
            }

            if ("powerpc".equals(arch)) {
                arch = "ppc";
            }

            return arch;
        }

        protected String getOsPrefix() {
            String osPrefix = this.getName().toLowerCase();
            int space = osPrefix.indexOf(" ");
            if (space != -1) {
                osPrefix = osPrefix.substring(0, space);
            }

            return osPrefix;
        }
    }

    static class Windows extends OperatingSystem {
        private final String nativePrefix = this.resolveNativePrefix();

        Windows() {
        }

        public boolean isWindows() {
            return true;
        }

        public String getFamilyName() {
            return "windows";
        }

        public String getScriptName(String scriptPath) {
            return FileUtils.withExtension(scriptPath, ".bat");
        }

        public String getExecutableSuffix() {
            return ".exe";
        }

        public String getExecutableName(String executablePath) {
            return FileUtils.withExtension(executablePath, ".exe");
        }

        public String getSharedLibrarySuffix() {
            return ".dll";
        }

        public String getSharedLibraryName(String libraryPath) {
            return FileUtils.withExtension(libraryPath, ".dll");
        }

        public String getLinkLibrarySuffix() {
            return ".lib";
        }

        public String getLinkLibraryName(String libraryPath) {
            return FileUtils.withExtension(libraryPath, ".lib");
        }

        public String getStaticLibrarySuffix() {
            return ".lib";
        }

        public String getStaticLibraryName(String libraryName) {
            return FileUtils.withExtension(libraryName, ".lib");
        }

        public String getNativePrefix() {
            return this.nativePrefix;
        }

        private String resolveNativePrefix() {
            String arch = System.getProperty("os.arch");
            if ("i386".equals(arch)) {
                arch = "x86";
            }

            return "win32-" + arch;
        }

        public String getPathVar() {
            return "Path";
        }
    }
}
