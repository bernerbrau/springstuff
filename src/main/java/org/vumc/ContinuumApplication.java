package org.vumc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.WebApplicationInitializer;
import rx.Observable;
import rx.Observer;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

@SpringBootApplication
public class ContinuumApplication
    extends SpringBootServletInitializer
    implements WebApplicationInitializer
{

  public static void main(String[] args)
  {
    new SpringApplicationBuilder(ContinuumApplication.class)
        .run(args);
  }

  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder)
  {
    return builder.sources(ContinuumApplication.class);
  }

  // RxJava patient stream
  private Subject<Patient, Patient> patientSubject = PublishSubject.<Patient>create().toSerialized();

  @Bean(name = "patientObserver")
  Observer<Patient> patientObserver() {
    return patientSubject;
  }

  @Bean(name = "patientObservable")
  Observable<Patient> patientObservable() {
    return patientSubject;
  }

}
