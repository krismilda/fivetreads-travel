package lt.fivethreads.storage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.org.apache.xpath.internal.operations.Mult;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

    private final Path rootLocation = Paths.get("upload-dir");

    public void store(MultipartFile file, String filename) {
        try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));
        } catch (Exception e) {
            throw new RuntimeException("Upload failed.");
        }
    }

    public Resource loadFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Something went wrong. Maybe file doesnt exist?");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Something went wrong.");
        }
    }

    public void deleteOneFile(String filename){
        FileSystemUtils.deleteRecursively(this.rootLocation.resolve(filename).toFile());
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage.");
        }
    }
}