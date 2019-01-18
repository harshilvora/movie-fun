package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class S3Store implements BlobStore {
    private AmazonS3Client s3Client;
    private  String photoStorageBucket;



    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;
    }


    @Override
    public void put(Blob blob) throws IOException {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(blob.contentType);

            s3Client.putObject(
                    photoStorageBucket,
                    blob.name,
                    blob.inputStream,objectMetadata
            );



    }

    @Override
    public Optional<Blob> get(String name) throws IOException {


            if(s3Client.doesObjectExist(photoStorageBucket,name)) {
                S3Object s3Object = s3Client.getObject(photoStorageBucket, name);
                Blob blob = new Blob(name, s3Object.getObjectContent(),s3Object.getObjectMetadata().getContentType());
                return Optional.of(blob);
            }

        return Optional.empty();
    }

    @Override
    public void deleteAll() {

    }
}
