package com.studysonic.settings;

import com.studysonic.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

//폼을 채울 객체 데이터
@Data
@NoArgsConstructor //얘를 만들어줘야지 컨트롤러에서 요청하는 메소드에 NPE가 생기지 않음
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
