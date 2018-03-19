package wt.connectfourgame.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import wt.connectfourgame.entity.GameEntity;

public interface GameEntityRepository extends JpaRepository<GameEntity, Long>{

	public Optional<GameEntity> findByPlayerRedToken(String playerRedToken);
	public Optional<GameEntity> findByPlayerYellowToken(String playerYellowToken);
}
