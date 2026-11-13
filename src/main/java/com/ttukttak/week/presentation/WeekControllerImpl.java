package com.ttukttak.week.presentation;

import com.ttukttak.week.application.WeekFacade;
import com.ttukttak.week.presentation.request.PressRequest;
import com.ttukttak.week.presentation.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/weeks")
@RequiredArgsConstructor
public class WeekControllerImpl implements WeekController {
    private final WeekFacade weekFacade;

    @Override
    @GetMapping("/current")
    public CurrentWeekResponse current() {
        return weekFacade.current();
    }

    @Override
    @GetMapping
    public WeekListResponse archive(@RequestParam(required = false) String cursor,
                                    @RequestParam(defaultValue = "12") int limit) {
        return weekFacade.archive(cursor, limit);
    }

    @Override
    @GetMapping("/{weekId}")
    public WeekDetailResponse get(@PathVariable String weekId) {
        return weekFacade.get(weekId);
    }

    @Override
    @PostMapping("/current/presses")
    public PressResponse press(@Valid @RequestBody PressRequest request) {
        return weekFacade.press(request.count());
    }

    @Override
    @PostMapping("/current/participation")
    public ParticipationResponse join(Principal principal) {
        return weekFacade.join(UUID.fromString(principal.getName()));
    }

    @Override
    @DeleteMapping("/current/participation")
    public ParticipationResponse leave(Principal principal) {
        return weekFacade.leave(UUID.fromString(principal.getName()));
    }
}
