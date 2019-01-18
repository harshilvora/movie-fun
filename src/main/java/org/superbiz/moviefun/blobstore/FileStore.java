package org.superbiz.moviefun.blobstore;

import javassist.bytecode.ByteArray;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

public class FileStore implements BlobStore {


    @Override
    public void put(Blob blob) throws IOException {
        File file = new File(blob.name);
        int read = 0;
        byte[] bytes = new byte[1024];
        FileOutputStream outputStream =
                new FileOutputStream(file);

        while ((read = blob.inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }

    }


    @Override
    public Optional<Blob> get(String name) throws IOException {
        File file = new File(name);

            if(file.exists()){
                Blob blob = new Blob(file.getName(),Files.newInputStream(file.toPath()),Files.probeContentType(file.toPath()));
                 return  Optional.ofNullable(blob);
            }

//            Path coverFilePath = getExistingCoverPath(albumId);
//            this.path = coverFilePath;
//            InputStream is = Files.newInputStream(coverFilePath);
//            String mimeType = Files.probeContentType(coverFilePath);
//            Blob blob = new Blob(coverFilePath.getFileName().toString(), is,mimeType)
//             else
//                return Optional.empty();


        return Optional.empty();
    }

    @Override
    public void deleteAll() {
        // ...
    }


}