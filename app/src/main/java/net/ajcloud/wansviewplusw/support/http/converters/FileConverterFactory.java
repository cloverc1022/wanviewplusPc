package net.ajcloud.wansviewplusw.support.http.converters;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class FileConverterFactory extends Converter.Factory {

    private String filePath;

    public FileConverterFactory(String filePath) {
        this.filePath = filePath;
    }

    public static FileConverterFactory create(String filePath) {
        return new FileConverterFactory(filePath);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new FileConverter(filePath);
    }
}
