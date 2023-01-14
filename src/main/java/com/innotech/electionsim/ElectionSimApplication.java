package com.innotech.electionsim;

import com.innotech.electionsim.controller.ElectionRunner;
import com.innotech.electionsim.data.ElectionSettings;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class ElectionSimApplication implements CommandLineRunner {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ApplicationContext context = new SpringApplicationBuilder(ElectionSimApplication.class)
				.web(WebApplicationType.NONE)
				.headless(false)
				.bannerMode(Banner.Mode.OFF)
				.run();
	}

	@Override
	public void run(String... args) {
		try {
			ElectionSettings savedSettings = ElectionSettings.load();
			ElectionRunner campaign = ElectionRunner.getInstance(savedSettings);
			campaign.start();
		} catch (IOException e) {
			System.out.println("Unable to load election settings; aborting start...");
		}
	}
}
