package wt.connectfourgame.queue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.Synchronized;
import wt.connectfourgame.exception.NicknameIsAlreadyInUseException;

@Component
public class OpponentQueue {

	private Map<String, String> nicknameMap;

	public OpponentQueue() {
		nicknameMap = new HashMap<String, String>();
	}

	@Synchronized
	public Optional<Entry<String, String>> findOpponent() {
		if (nicknameMap.isEmpty())
			return Optional.empty();
		Entry<String, String> entry = nicknameMap.entrySet().iterator().next();
		nicknameMap.clear();
		return Optional.of(entry);
	}

	public Entry<String, String> registerNickname(String nickname) throws NicknameIsAlreadyInUseException {
		if (isNicknameExist(nickname))
			throw new NicknameIsAlreadyInUseException(nickname);
		nicknameMap.put(nickname, UUID.randomUUID().toString());
		return nicknameMap.entrySet().iterator().next();
	}

	private boolean isNicknameExist(String nickname) {
		return nickname.contains(nickname);
	}

}
