package nextstep.subway.path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.acceptance.LineSectionTestMethod;
import nextstep.subway.line.acceptance.LineTestMethod;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("최단 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {
	private LineResponse 신분당선;
	private LineResponse 이호선;
	private LineResponse 삼호선;
	private StationResponse 강남역;
	private StationResponse 양재역;
	private StationResponse 교대역;
	private StationResponse 남부터미널역;

	@BeforeEach
	public void setUp() {
		super.setUp();

		강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
		양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
		교대역 = StationAcceptanceTest.지하철역_등록되어_있음("교대역").as(StationResponse.class);
		남부터미널역 = StationAcceptanceTest.지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

		LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
		LineRequest lineRequest2 = new LineRequest("이호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 10);
		LineRequest lineRequest3 = new LineRequest("삼호선", "bg-orange-600", 교대역.getId(), 양재역.getId(), 5);

		신분당선 = LineTestMethod.지하철_노선_등록되어_있음(lineRequest1).as(LineResponse.class);
		이호선 = LineTestMethod.지하철_노선_등록되어_있음(lineRequest2).as(LineResponse.class);
		삼호선 = LineTestMethod.지하철_노선_등록되어_있음(lineRequest3).as(LineResponse.class);

		LineSectionTestMethod.지하철_노선에_지하철역_등록_요청(삼호선, 교대역, 남부터미널역, 3);
	}

	@DisplayName("최단 경로 조회 시나리오")
	@Test
	void shortestPathFindScenario() {
		// Backgroud
		// Given : 지하철역 등록되어 있음
		// And : 지하철 노선 등록되어 있음
		// And : 지하철 노선에 지하철역 등록되어 있음

		// Scenario : 다양한 지하철 최단 경로 조회
		// When : 미환승역에서 환승역으로 최단거리 조회
		// Then : 해당 역 리스트 리턴
		// When : 환승역에서 미환승역으로 최단거리 조회
		// Then : 해당 역 리스트 리턴
		// When : 환승역에서 환승역으로 최단거리 조회
		// Then : 해당 역 리스트 리턴
	}

	@DisplayName("최단 경로 조회 시 오류 시나리오")
	@Test
	void shortestPathFindErrorScenario() {
		// Backgroud
		// Given : 지하철역 등록되어 있음
		// And : 지하철 노선 등록되어 있음
		// And : 지하철 노선에 지하철역 등록되어 있음

		// Scenario : 최단 경로 조회 시 오류 시나리오
		// When : 출발역과 도착역이 같은 경우
		// Then : 에러 발생
		// When : 출발역과 도착역이 연결되어 있지 않음
		// Then : 에러 발생
		// When : 출발역 혹은 도착역이 존재하지 않음
		// Then : 에러 발생
	}
}
