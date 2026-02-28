package com.svrmslk.common.events.core;

/**
 * Semantic version for event schemas.
 * Supports schema evolution and backward compatibility.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public record EventVersion(int major, int minor, int patch) implements Comparable<EventVersion> {

    public EventVersion {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version numbers must be non-negative");
        }
    }

    /**
     * Initial version for new events.
     */
    public static final EventVersion V1_0_0 = new EventVersion(1, 0, 0);

    /**
     * Parses a version string in format "major.minor.patch".
     *
     * @param versionString the version string (e.g., "1.2.3")
     * @return parsed version
     */
    public static EventVersion parse(String versionString) {
        if (versionString == null || versionString.isBlank()) {
            throw new IllegalArgumentException("Version string cannot be null or blank");
        }

        String[] parts = versionString.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Version must be in format major.minor.patch: " + versionString);
        }

        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = Integer.parseInt(parts[2]);
            return new EventVersion(major, minor, patch);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid version format: " + versionString, e);
        }
    }

    /**
     * Checks if this version is compatible with another version.
     * Compatible means same major version and this version is newer or equal.
     *
     * @param other the other version
     * @return true if compatible
     */
    public boolean isCompatibleWith(EventVersion other) {
        return this.major == other.major && this.compareTo(other) >= 0;
    }

    @Override
    public int compareTo(EventVersion other) {
        int majorCmp = Integer.compare(this.major, other.major);
        if (majorCmp != 0) return majorCmp;

        int minorCmp = Integer.compare(this.minor, other.minor);
        if (minorCmp != 0) return minorCmp;

        return Integer.compare(this.patch, other.patch);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}