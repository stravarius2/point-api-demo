package com.example.demo.meta;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointActionType {
    SAVE("적립"),
    USE("사용"),
    EXPIRED("만료");

   private final String description;

   public static String from(PointActionType pointActionType){
       return pointActionType.getDescription();
    }
}
