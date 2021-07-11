package nextstep.subway.favorite.application;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.favorite.domain.Favorite;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import nextstep.subway.member.domain.Member;
import nextstep.subway.member.domain.MemberRepository;
import nextstep.subway.station.application.StationService;

@Service
@Transactional
public class FavoriteService {
	public static final int ZERO = 0;
	private MemberRepository memberRepository;
	private StationService stationService;

	public FavoriteService(MemberRepository memberRepository, StationService stationService) {
		this.memberRepository = memberRepository;
		this.stationService = stationService;
	}

	public FavoriteResponse save(Long memberId, FavoriteRequest favoriteRequest) {
		Member member = memberRepository.findById(memberId).orElseThrow(RuntimeException::new);
		member.addFavorite(new Favorite(stationService.findStationById(favoriteRequest.getSourceId()), stationService.findStationById(favoriteRequest.getTargetId())));
		Member saveMember = memberRepository.save(member);

		return FavoriteResponse.of(getLatestFavorite(saveMember));
	}

	private Favorite getLatestFavorite(Member saveMember) {
		return saveMember.getFavorites().stream().max((x1, x2) -> (int) (x1.getId() - x2.getId())).orElseThrow(RuntimeException::new);
	}

	@Transactional(readOnly = true)
	public List<FavoriteResponse> find(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(RuntimeException::new);

		return getFavoriteResponses(member);
	}

	private List<FavoriteResponse> getFavoriteResponses(Member member) {
		if(member.getFavorites().size() <= ZERO) {
			throw new NoSuchElementException();
		}

		List<FavoriteResponse> favoriteResponses = new ArrayList<>();
		for (Favorite favorite : member.getFavorites()) {
			favoriteResponses.add(FavoriteResponse.of(favorite));
		}
		return favoriteResponses;
	}

	public void delete(Long memberId, Long favoriteId) {
		Member member = memberRepository.findById(memberId).orElseThrow(RuntimeException::new);
		member.deleteFavorite(favoriteId);
		memberRepository.save(member);
	}
}
