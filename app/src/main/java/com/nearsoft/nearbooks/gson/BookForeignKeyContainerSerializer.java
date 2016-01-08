package com.nearsoft.nearbooks.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.nearsoft.nearbooks.models.BorrowModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

import java.lang.reflect.Type;

/**
 * Serializer for book foreign key container.
 * Created by epool on 1/7/16.
 */
public class BookForeignKeyContainerSerializer
        implements JsonDeserializer<ForeignKeyContainer<Book>>,
        JsonSerializer<ForeignKeyContainer<Book>> {

    public static final Type TYPE = new TypeToken<ForeignKeyContainer<Book>>() {
    }.getType();

    @Override
    public ForeignKeyContainer<Book> deserialize(JsonElement json, Type typeOfT,
                                                 JsonDeserializationContext context)
            throws JsonParseException {
        return BorrowModel.bookForeignKeyContainerFromBookId(json.getAsString());
    }

    @Override
    public JsonElement serialize(ForeignKeyContainer<Book> src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        return new JsonPrimitive(BorrowModel.bookIdFromBookForeignKeyContainer(src));
    }

}
