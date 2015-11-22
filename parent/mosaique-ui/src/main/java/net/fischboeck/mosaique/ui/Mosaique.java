package net.fischboeck.mosaique.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.airhacks.afterburner.injection.Injector;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

@SpringBootApplication
public class Mosaique extends javafx.application.Application {

	@Autowired private AppBase		_base;
	
	private static String[] savedArgs;
	private ConfigurableApplicationContext appCtx;
	
	public static void main(String[] args) {
		savedArgs = args;
		launch(Mosaique.class, args);
	}
	
	@Override
	public void init() throws Exception {
		appCtx = SpringApplication.run(getClass(), savedArgs);
		appCtx.getAutowireCapableBeanFactory().autowireBean(this);
		Injector.setInstanceSupplier(appCtx::getBean);
	}
	

	@Override
	public void start(Stage stage) throws Exception {
			
		Scene s = new Scene(new StackPane());
		_base.setScene(s);
		stage.setScene(s);
		_base.showMainView();
		stage.show();	
	}
	
	@Override
	public void stop() throws Exception {
		appCtx.close();
	}
}
