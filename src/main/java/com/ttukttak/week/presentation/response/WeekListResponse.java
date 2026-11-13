package com.ttukttak.week.presentation.response;

import java.util.List;

/** GET /weeks 응답. 다음 페이지가 없으면 nextCursor가 null이에요. */
public record WeekListResponse(List<WeekSummary> items, String nextCursor) {
}
