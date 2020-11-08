package ruby.blacktech.libraunrar4j.rarfile;

// from lib unrar headers.hpp HOST_SYSTEM
public enum HostSystem {

    // RAR 5.0 host OS
    HOST5_WINDOWS((byte)0),
    HOST5_UNIX((byte)1),

    // RAR 3.0 host OS.
    HOST_MSDOS((byte)0),
    HOST_OS2((byte)1),
    HOST_WIN32((byte)2),
    HOST_UNIX((byte)3),
    HOST_MACOS((byte)4),
    HOST_BEOS((byte)5),

    // == from junrar
    msdos((byte) 0),
    os2((byte) 1),
    win32((byte) 2),
    unix((byte) 3),
    macos((byte) 4),
    beos((byte) 5);

    private final byte hostByte;

    public static HostSystem findHostSystem(byte hostByte) {
        if (HostSystem.msdos.equals(hostByte)) {
            return HostSystem.msdos;
        }
        if (HostSystem.os2.equals(hostByte)) {
            return HostSystem.os2;
        }
        if (HostSystem.win32.equals(hostByte)) {
            return HostSystem.win32;
        }
        if (HostSystem.unix.equals(hostByte)) {
            return HostSystem.unix;
        }
        if (HostSystem.macos.equals(hostByte)) {
            return HostSystem.macos;
        }
        if (HostSystem.beos.equals(hostByte)) {
            return HostSystem.beos;
        }
        return null;
    }


    HostSystem(byte hostByte) {
        this.hostByte = hostByte;
    }

    public boolean equals(byte hostByte) {
        return this.hostByte == hostByte;
    }

    public byte getHostByte() {
        return hostByte;
    }
    //???? public static final byte max = 6;
}
