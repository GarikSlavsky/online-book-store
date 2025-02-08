package mate.academy.onlinebookstore.service.user;

import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.onlinebookstore.dto.user.UserResponseDto;
import mate.academy.onlinebookstore.exceptions.EntityNotFoundException;
import mate.academy.onlinebookstore.exceptions.RegistrationException;
import mate.academy.onlinebookstore.mapper.UserMapper;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException(
                    "The user with email: " + requestDto.getEmail() + " already exists.");
        }

        User user = userMapper.intoModel(requestDto);
        return userMapper.intoUserDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id: " + id + "not found.")
        );
        return userMapper.intoUserDto(user);
    }
}
