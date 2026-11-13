package com.ttukttak.week.presentation;

import com.ttukttak.week.application.WeekFacade;
import com.ttukttak.week.presentation.request.ImportWeeksRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/weeks")
@RequiredArgsConstructor
public class AdminWeekControllerImpl implements AdminWeekController {
    private final WeekFacade weekFacade;

    @Override
    @PostMapping("/import")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void importWeeks(@Valid @RequestBody ImportWeeksRequest request) {
        weekFacade.importWeeks(request);
    }
}
