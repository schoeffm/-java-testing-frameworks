package de.bender.testing.memoryfilesystem;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.FileSystem;

public class FileSystemExtension implements BeforeEachCallback, AfterEachCallback {

    private FileSystem fileSystem;

    FileSystem getFileSystem() {
        return this.fileSystem;
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        this.fileSystem = MemoryFileSystemBuilder.newEmpty().build("name");
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (this.fileSystem != null) {
            this.fileSystem.close();
        }
    }
}
