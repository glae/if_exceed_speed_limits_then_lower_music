package glae.speedlimitsmusic;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NokiaHereRoundingTest {


    @Test
    public void given_Nokia_HERE_meters_per_second_measure_I_want_to_get_it_as_a_legal_limit_in_kmph() throws Exception {

        //30
        //http://route.st.nlp.nokia.com/routing/6.2/getlinkinfo.json?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg&waypoint=47.36562491402572,0.6796278992999589

        double nokiaHereSpeed = 8.33;
        int legalLimit= (int) Math.round(nokiaHereSpeed * 3.6);
        assertThat(legalLimit).isEqualTo(30);

//50
       // http://route.st.nlp.nokia.com/routing/6.2/getlinkinfo.json?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg&waypoint=47.363517491765194,0.680700782905916
        nokiaHereSpeed = 13.89;
        legalLimit= (int) Math.round(nokiaHereSpeed * 3.6);
        assertThat(legalLimit).isEqualTo(50);

        //70
        //http://route.st.nlp.nokia.com/routing/6.2/getlinkinfo.json?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg&waypoint=47.36748518922991,0.6571402589190996
        nokiaHereSpeed = 19.44;
        legalLimit= (int) Math.round(nokiaHereSpeed * 3.6);
        assertThat(legalLimit).isEqualTo(70);

        //90
        //http://route.st.nlp.nokia.com/routing/6.2/getlinkinfo.json?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg&waypoint=47.36905474540249,0.6652083436358964
        nokiaHereSpeed = 25.0;
        legalLimit= (int) Math.round(nokiaHereSpeed * 3.6);
        assertThat(legalLimit).isEqualTo(90);

        //110
        //http://route.st.nlp.nokia.com/routing/6.2/getlinkinfo.json?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg&waypoint=47.306968675552824,0.6393062024116887
        nokiaHereSpeed = 30.56;
        legalLimit= (int) Math.round(nokiaHereSpeed * 3.6);
        assertThat(legalLimit).isEqualTo(110);


        //130
        //http://route.st.nlp.nokia.com/routing/6.2/getlinkinfo.json?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg&waypoint=47.306968675552824,0.6393062024116887
        nokiaHereSpeed = 36.11;
        legalLimit= (int) Math.round(nokiaHereSpeed * 3.6);
        assertThat(legalLimit).isEqualTo(130);

    }
}
