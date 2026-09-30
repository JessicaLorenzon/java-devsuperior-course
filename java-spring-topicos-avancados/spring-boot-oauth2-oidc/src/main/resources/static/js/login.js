(function () {
    const input = document.getElementById('password');
    const toggle = document.getElementById('togglePassword');
    if (!input || !toggle) return;

    const eyeOn = toggle.querySelector('.icon-eye');
    const eyeOff = toggle.querySelector('.icon-eye-off');

    toggle.addEventListener('click', function () {
        const wasHidden = input.type === 'password';
        input.type = wasHidden ? 'text' : 'password';
        eyeOn.classList.toggle('is-hidden', wasHidden);
        eyeOff.classList.toggle('is-hidden', !wasHidden);
        toggle.setAttribute('aria-pressed', String(wasHidden));
        toggle.setAttribute('aria-label', wasHidden ? 'Esconder senha' : 'Mostrar senha');
    });
})();
