package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {
	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	/**
	 * Practice 를 위한 Dependency 추가 > Response Object 를 별도로 생성하여 관리할 경우, 아래 Hibernate5Module 필요 없음
	 * 	=> LazyLoading 을 선언한 필드를 조회 하기 위함
	 */

	@Bean
	Hibernate5Module hibernate5Module() {
		// 아래와 같이 FORCE_LAZY_LOADING 을 통해 LAZY 이슈를 해결하여 Entity 를 반환하기 보다는 Response 용 DTO 를 생성하여 관리하는 것이 더 나은 설계
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		// hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return hibernate5Module;
	}
}
