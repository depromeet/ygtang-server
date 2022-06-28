package inspiration.tasklets;

import inspiration.member.MemberService;
import inspiration.member.response.MemberResponse;
import inspiration.slack.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class InspirationNotiTasklet implements Tasklet {

    private final MemberService memberService;
    private final AlertService alertService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext){

        log.info("executed tasklet !!");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-----영감탱 회원가입 현황-----");
        stringBuilder.append("\n");

        List<MemberResponse> memberResponses = memberService.findAll();

        for (MemberResponse memberResponse : memberResponses) {
            stringBuilder.append(memberResponse.getEmail() + " / " + memberResponse.getNickName() + " / " + memberResponse.getCreatedDateTime());
            stringBuilder.append("\n");
        }
        alertService.slackSendMessage(stringBuilder.toString());
        return RepeatStatus.FINISHED;
    }
}
