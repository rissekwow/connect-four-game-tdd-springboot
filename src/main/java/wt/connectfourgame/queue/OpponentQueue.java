package wt.connectfourgame.queue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.Synchronized;
import wt.connectfourgame.exception.NicknameIsAlreadyInUseException;

@Component
public class OpponentQueue {

	private Map<String, String> nicknameMap;
	private Map<String, String> nicknameWaitingMap;

	public OpponentQueue() {
		nicknameWaitingMap = new HashMap<String, String>();
		nicknameMap = new HashMap<String, String>();
	}

	@Synchronized
	public Optional<Entry<String, String>> findOpponent() {
		if (nicknameWaitingMap.isEmpty())
			return Optional.empty();
		Entry<String, String> entry = nicknameWaitingMap.entrySet().iterator().next();
		nicknameWaitingMap.clear();
		return Optional.of(entry);
	}

	@Synchronized
	public void registerNickname(String nickname, String token) throws NicknameIsAlreadyInUseException {
		if (isNicknameExist(nickname))
			throw new NicknameIsAlreadyInUseException(nickname);
		nicknameMap.put(nickname, token);
	}

	@Synchronized
	public void addNicknameToWaitingQueue(String nickname) {
		nicknameWaitingMap.put(nickname, nicknameMap.get(nickname));
	}

	public void removeNicknameByToken(String token) {
		nicknameMap.values().removeIf(value -> value.equals(token));
	}

	@Synchronized
	public String getNicknameToken(String nickname) {
		return nicknameMap.get(nickname);
	}

	@Synchronized
	private boolean isNicknameExist(String nickname) {
		return nicknameMap.containsKey(nickname);
	}

}
