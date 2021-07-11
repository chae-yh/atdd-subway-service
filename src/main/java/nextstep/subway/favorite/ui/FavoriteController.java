package nextstep.subway.favorite.ui;

import java.net.URI;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.auth.domain.AuthenticationPrincipal;
import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.favorite.application.FavoriteService;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
	private FavoriteService favoriteService;

	public FavoriteController(FavoriteService favoriteService) {
		this.favoriteService = favoriteService;
	}

	@PostMapping
	public ResponseEntity createFavorite(@AuthenticationPrincipal LoginMember loginMember, @RequestBody FavoriteRequest favoriteRequest) {
		FavoriteResponse favoriteResponse = favoriteService.save(loginMember.getId(), favoriteRequest);
		return ResponseEntity.created(URI.create("/favorites/" + favoriteResponse.getId())).build();
	}

	@GetMapping
	public ResponseEntity findFavorite(@AuthenticationPrincipal LoginMember loginMember) {
		return ResponseEntity.ok().body(favoriteService.find(loginMember.getId()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity deleteFavorite(@AuthenticationPrincipal LoginMember loginMember, @PathVariable Long id) {
		favoriteService.delete(loginMember.getId(), id);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity handleRuntimeException(RuntimeException runtimeException) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity handleRuntimeException(NoSuchElementException noSuchElementException) {
		return ResponseEntity.noContent().build();
	}
}
