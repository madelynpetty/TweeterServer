package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class S3DAO {
    private static final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-west-2").build();

    public static void putUrl(String alias, String imageUrl) {
        byte[] byteArray = Base64.getDecoder().decode(imageUrl);
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        s3.putObject(new PutObjectRequest("maddiepettytweeterbucket", alias,
                inputStream, new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public static String getUrl(String alias) {
        return s3.getUrl("maddiepettytweeterbucket", alias).toString();
    }
}
