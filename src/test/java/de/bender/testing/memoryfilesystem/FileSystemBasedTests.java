package de.bender.testing.memoryfilesystem;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileSystemBasedTests {

    /**
     * There is an experimental feature in JUnit5 {@link org.junit.jupiter.api.io.TempDir} that can be used in a
     * similar fashion - the biggest difference is that this approach here doesn't even touch the underlying file
     * system but handles everything in memory only. That's cleaner in the end and more portable.
     */
    @Test
    @DisplayName("Use in-memory file-system to handle files that doesn't even exist")
    void writeIntoFileThatOnlyExistsInMemory() throws IOException {
        try (FileSystem fileSystem = MemoryFileSystemBuilder.newEmpty().build()) {
            // given
            Path p = fileSystem.getPath( "File.txt");

            // when
            Path write = Files.write(p, List.of("This", "is", "Sparta!!!"));

            // then
            assertThat(Files.readAllLines(write)).containsExactly("This", "is", "Sparta!!!");

        }
    }

    @Nested
    class WithRegisteredExtension {
        @RegisterExtension
        final FileSystemExtension extension = new FileSystemExtension();

        @Test
        @DisplayName("Uses an Extension to initialize the in-memory Filesystem")
        void usesAnExtensionToExternalizeBoilerplate() {
            FileSystem fileSystem = extension.getFileSystem();

            Path path = fileSystem.getPath("this", "is", "sparta.txt");

            assertThat(Files.exists(path)).isFalse();
        }
    }
}
