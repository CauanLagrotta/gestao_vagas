package br.com.lagrotta.gestao_vagas.modules.company.controllers;

import br.com.lagrotta.gestao_vagas.exceptions.CompanyNotFoundException;
import br.com.lagrotta.gestao_vagas.modules.company.dto.CreateJobDTO;
import br.com.lagrotta.gestao_vagas.modules.company.entities.CompanyEntity;
import br.com.lagrotta.gestao_vagas.modules.company.repositories.CompanyRepository;
import br.com.lagrotta.gestao_vagas.utils.TestUtils;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CreateJobControllerTest {

  private MockMvc mvc;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private CompanyRepository companyRepository;

  @Before
  public void setup() {
    mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(SecurityMockMvcConfigurers.springSecurity())
        .build();
  }

  @Test
  public void should_be_able_to_create_a_new_job() throws Exception {
    var company = CompanyEntity.builder()
        .description("COMPANY DESCRIPTION")
        .email("company@email.com")
        .password("1234567890")
        .username("COMPANY_USERNAME")
        .name("COMPANY NAME")
        .build();

    company = companyRepository.saveAndFlush(company);

    var createJobDTO = CreateJobDTO.builder()
        .benefits("BENEFITS_TEST")
        .description("DESCRIPTION_TEST")
        .level("LEVEL_TEST")
        .build();

    var result = mvc.perform(MockMvcRequestBuilders.post("/company/job/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtils.objectToJSON(createJobDTO))
            .header("Authorization",
                TestUtils.generateToken(company.getId(), "SeCr3T_t0K3N!"))
        )
        .andExpect(MockMvcResultMatchers.status().isOk());

    System.out.println(result);
  }

  @Test
  public void should_not_be_able_to_create_a_new_job_if_company_not_found() throws Exception{
    var createJobDTO = CreateJobDTO.builder()
        .benefits("BENEFITS_TEST")
        .description("DESCRIPTION_TEST")
        .level("LEVEL_TEST")
        .build();

    mvc.perform(MockMvcRequestBuilders.post("/company/job/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestUtils.objectToJSON(createJobDTO))
        .header("Authorization",
            TestUtils.generateToken(UUID.randomUUID(), "SeCr3T_t0K3N!")))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
}
