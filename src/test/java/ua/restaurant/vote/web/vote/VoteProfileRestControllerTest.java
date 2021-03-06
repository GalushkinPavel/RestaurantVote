package ua.restaurant.vote.web.vote;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import ua.restaurant.vote.RestaurantTestData;
import ua.restaurant.vote.VoteTestData;
import ua.restaurant.vote.model.Vote;
import ua.restaurant.vote.util.DateTimeUtil;
import ua.restaurant.vote.web.AbstractControllerTest;
import ua.restaurant.vote.web.json.JsonUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.restaurant.vote.RestaurantTestData.*;
import static ua.restaurant.vote.TestUtil.userHttpBasic;
import static ua.restaurant.vote.UserTestData.*;
import static ua.restaurant.vote.VoteTestData.MATCHER;
import static ua.restaurant.vote.VoteTestData.*;

/**
 * Created by Galushkin Pavel on 13.03.2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VoteProfileRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = VoteProfileRestController.REST_URL + '/';

    @Test
    @Transactional
    public void testCreate() throws Exception {
        mockMvc.perform(post(REST_URL + "{restaurantId}", RESTAURANT2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER1))
                .content(JsonUtil.writeValue(RESTAURANT2_ID)))
                .andDo(print())
                .andExpect(status().isOk());

        Vote returned = voteService.getVote(USER1_ID, LocalDate.now());
        Vote created = VoteTestData.getCreated();
        created.setRestaurant(RESTAURANT2);
        created.setUser(USER1);
        created.setId(100023);

        MATCHER.assertEquals(created, returned);
        MATCHER.assertCollectionEquals(Arrays.asList(returned, VOTE6, VOTE2), voteService.getAll(USER1_ID));
    }

    @Test
    @Transactional
    public void testUpdate() throws Exception {
        DateTimeUtil.setDeadlineVoteTime(LocalTime.now().plusMinutes(1));
        Vote expected = voteService.getVote(USER2_ID, LocalDate.now());

        mockMvc.perform(post(REST_URL + "{restaurantId}", RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER2))
                .content(JsonUtil.writeValue(RESTAURANT1_ID)))
                .andDo(print())
                .andExpect(status().isOk());

        DateTimeUtil.setDeadlineVoteTime(DateTimeUtil.DEFAULT_VOTE_DEADLINE_TIME);
        Vote updated = voteService.getVote(USER2_ID, LocalDate.now());
        MATCHER.assertEquals(expected, updated);
        RestaurantTestData.MATCHER.assertEquals(RESTAURANT1, updated.getRestaurant());
    }

    @Test
    public void testUpdateAfterDeadLine() throws Exception {
        DateTimeUtil.setDeadlineVoteTime(LocalTime.now().minusMinutes(1));

        mockMvc.perform(put(REST_URL + "{restaurantId}", RESTAURANT2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER1))
                .content(JsonUtil.writeValue(RESTAURANT2_ID)))
                .andExpect(status().is4xxClientError());

        DateTimeUtil.setDeadlineVoteTime(DateTimeUtil.DEFAULT_VOTE_DEADLINE_TIME);
    }
}
