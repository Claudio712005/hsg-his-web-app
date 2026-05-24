var hsgLogin = (function () {

  function $(id) { return document.getElementById(id); }

  function togglePassword() {
    var field    = $('password');
    var eyeOpen  = $('eyeOpen');
    var eyeClosed = $('eyeClosed');
    var btn      = $('toggleEye');

    if (!field) return;

    var isPassword = field.type === 'password';
    field.type = isPassword ? 'text' : 'password';

    if (eyeOpen)   eyeOpen.style.display   = isPassword ? 'none' : '';
    if (eyeClosed) eyeClosed.style.display = isPassword ? ''     : 'none';

    btn.setAttribute('aria-label', isPassword ? 'Ocultar senha' : 'Mostrar senha');
  }

  function onSubmit(form) {
    var btn      = $('kc-login');
    var spinner  = $('spinner');
    var btnLabel = $('btnLabel');

    if (btn) {
      btn.classList.add('is-loading');
      btn.disabled = true;
    }
    if (spinner)  spinner.style.display = 'inline-block';
    if (btnLabel) btnLabel.textContent  = 'Autenticando...';

    return true;
  }

  function clearErrorOnInput() {
    var username = $('username');
    var password = $('password');
    var userWrap = $('userWrap');
    var passWrap = $('passWrap');

    function clearError() {
      if (userWrap) userWrap.classList.remove('has-error');
      if (passWrap) passWrap.classList.remove('has-error');
    }

    if (username) username.addEventListener('input', clearError);
    if (password) password.addEventListener('input', clearError);
  }

  function init() {
    clearErrorOnInput();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }

  return {
    togglePassword: togglePassword,
    onSubmit: onSubmit
  };

}());
