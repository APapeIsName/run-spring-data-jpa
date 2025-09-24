package study.data_jpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UserNameOnly {
//    String getUsername(); // = close projection

    @Value("#{target.username + ' ' + target.age}")
    String getUserName(); // = open projection
}
