package com.studysonic.settings;

import com.studysonic.domain.Account;
import lombok.Data;

//폼을 채울 객체 데이터
@Data
public class Profile {

    private String bio;

    private String url;

    private String occupation;

    private String location; //varchar 255

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
