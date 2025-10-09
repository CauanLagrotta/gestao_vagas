package br.com.lagrotta.gestao_vagas.modules.candidate.useCases;

import br.com.lagrotta.gestao_vagas.exceptions.JobNotFoundException;
import br.com.lagrotta.gestao_vagas.exceptions.UserNotFoundException;
import br.com.lagrotta.gestao_vagas.modules.candidate.entity.ApplyJobEntity;
import br.com.lagrotta.gestao_vagas.modules.candidate.entity.CandidateEntity;
import br.com.lagrotta.gestao_vagas.modules.candidate.repository.ApplyJobRepository;
import br.com.lagrotta.gestao_vagas.modules.candidate.repository.CandidateRepository;
import br.com.lagrotta.gestao_vagas.modules.company.entities.JobEntity;
import br.com.lagrotta.gestao_vagas.modules.company.repositories.JobRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ApplyJobCandidateUseCaseTest {
  @InjectMocks
  private ApplyJobCandidateUseCase applyJobCandidateUseCase;

  @Mock
  private CandidateRepository candidateRepository;

  @Mock
  private JobRepository jobRepository;

  @Mock
  private ApplyJobRepository applyJobRepository;

  @Test
  @DisplayName("Should not be able to apply job with candidate not found")
  public void should_not_be_able_to_apply_job_with_candidate_not_found(){
    try{
      applyJobCandidateUseCase.execute(null, null);
    }catch (Exception e){
      Assertions.assertThat(e).isInstanceOf(UserNotFoundException.class);
    }
  }

  @Test
  @DisplayName("Should not be able to apply job with job not found")
  public void should_not_be_able_to_apply_job_with_job_not_found(){
    var idCandidate = UUID.randomUUID();
    var candidate = new CandidateEntity();
    candidate.setId(idCandidate);

    Mockito.when(candidateRepository.findById(idCandidate)).thenReturn(Optional.of(candidate));

    try{
      applyJobCandidateUseCase.execute(idCandidate, null);

    }catch (Exception e){
      Assertions.assertThat(e).isInstanceOf(JobNotFoundException.class);
    }
  }


  @Test
  @DisplayName("Should be able to apply to a job")
  public void should_be_able_to_create_a_new_apply_job(){
    var idCandidate = UUID.randomUUID();
    var idJob = UUID.randomUUID();

    var applyJob = ApplyJobEntity.builder().candidateId(idCandidate).jobId(idJob).build();

    var applyJobCreated = ApplyJobEntity.builder().id(UUID.randomUUID()).build();

    Mockito.when(candidateRepository.findById(idCandidate)).thenReturn(Optional.of(new CandidateEntity()));
    Mockito.when(jobRepository.findById(idJob)).thenReturn(Optional.of(new JobEntity()));

    Mockito.when(applyJobRepository.save(applyJob)).thenReturn(applyJobCreated);

    var result = applyJobCandidateUseCase.execute(idCandidate, idJob);

    Assertions.assertThat(result).hasFieldOrProperty("id");
    org.junit.jupiter.api.Assertions.assertNotNull(result.getId());
  }
}
