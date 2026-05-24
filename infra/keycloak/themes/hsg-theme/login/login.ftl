<!DOCTYPE html>
<html lang="${locale!'pt-BR'}">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta name="robots" content="noindex, nofollow"/>
    <title>${msg("loginTitle", realm.displayName)}</title>

    <link rel="preconnect" href="https://fonts.googleapis.com"/>
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin=""/>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&amp;display=swap" rel="stylesheet"/>

    <link rel="stylesheet" href="${url.resourcesPath}/css/styles.css"/>
</head>
<body>

<div class="page">

    <section class="panel-left" aria-hidden="true">

        <div class="brand">
            <div class="mark">H</div>
            <div class="wordmark">
                <span class="wordmark-name">HSG</span>
                <span class="wordmark-sub">Hospital System Group</span>
            </div>
        </div>

        <div class="hero">
            <div class="eyebrow">
                <span class="pulse"></span>
                Plataforma Hospitalar Integrada
            </div>
            <h1>
                Cuidado conectado,<br>
                <span class="accent">decisões confiáveis.</span>
            </h1>
            <p>
                Acesse prontuários, agenda de atendimentos e indicadores clínicos
                em um único ambiente seguro. A plataforma HSG é utilizada por mais
                de 120 unidades hospitalares em todo o Brasil.
            </p>
        </div>

        <div class="art">

            <div class="cross"></div>

            <div class="card-float c1">
                <div class="art-row">
                    <div class="avatar">MR</div>
                    <div>
                        <div class="art-name">Maria Ribeiro</div>
                        <div class="art-role">Enfermagem &middot; UTI Adulto</div>
                    </div>
                </div>
                <div class="art-label">Sinais vitais &middot; Leito 07</div>
                <div class="progress-bar"><span style="width:72%"></span></div>
                <div class="art-meta">
                    <span>PA 128/82</span>
                    <span>FC 84</span>
                    <span>SpO&#8322; 97%</span>
                </div>
            </div>

            <div class="card-float c2">
                <div class="art-title">Atendimentos hoje</div>
                <div class="art-value">184</div>
                <div class="art-trend">&#9650; +6% vs. ontem</div>
                <div class="sparkline">
                    <span style="height:40%"></span>
                    <span style="height:65%"></span>
                    <span style="height:50%"></span>
                    <span style="height:80%"></span>
                    <span style="height:60%"></span>
                    <span style="height:90%"></span>
                    <span style="height:75%"></span>
                </div>
            </div>

        </div>

        <div class="trust">
            <div class="trust-item">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#a8f0e4" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                </svg>
                <div>
                    <strong>LGPD</strong>
                    <span>Conformidade total</span>
                </div>
            </div>
            <div class="trust-item">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#a8f0e4" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <div>
                    <strong>SSL/TLS</strong>
                    <span>Conexão cifrada</span>
                </div>
            </div>
            <div class="trust-item">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#a8f0e4" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <polyline points="9 11 12 14 22 4"/>
                    <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
                </svg>
                <div>
                    <strong>ANVISA</strong>
                    <span>Regulamentado</span>
                </div>
            </div>
        </div>

    </section>

    <section class="panel-right">

        <div class="card">

            <div class="logo-mobile">
                <div class="mark-sm">H</div>
                <span class="wordmark-name">HSG</span>
            </div>

            <h2>${msg("loginTitle", realm.displayName)}</h2>
            <p class="card-sub">${msg("loginSubtitle")}</p>

            <#if message?has_content>
                <div class="alert alert-${message.type}" role="alert" aria-live="assertive">
                    <#if message.type == 'error'>
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <circle cx="12" cy="12" r="10"/>
                            <line x1="12" y1="8" x2="12" y2="12"/>
                            <line x1="12" y1="16" x2="12.01" y2="16"/>
                        </svg>
                    <#elseif message.type == 'warning'>
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                            <line x1="12" y1="9" x2="12" y2="13"/>
                            <line x1="12" y1="17" x2="12.01" y2="17"/>
                        </svg>
                    <#else>
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <polyline points="20 6 9 17 4 12"/>
                        </svg>
                    </#if>
                    <span>${message.summary?no_esc}</span>
                </div>
            </#if>

            <form id="kc-form-login"
                  action="${url.loginAction}"
                  method="post"
                  onsubmit="hsgLogin.onSubmit(this)">

                <input type="hidden" id="id-hidden-input" name="credentialId"
                       value="${(auth.selectedCredential)!''}"/>

                <div class="field">
                    <label for="username">
                        <#if !realm.loginWithEmailAllowed>${msg("username")}
                        <#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}
                        <#else>${msg("email")}
                        </#if>
                    </label>
                    <div class="input-wrap <#if messagesPerField.existsError('username','password')>has-error</#if>"
                         id="userWrap">
            <span class="input-icon" aria-hidden="true">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                   stroke-linecap="round" stroke-linejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </span>
                        <input tabindex="1"
                               id="username"
                               name="username"
                               type="text"
                               value="${(login.username)!''}"
                               autocomplete="username"
                               autofocus
                               spellcheck="false"
                               <#if usernameEditDisabled??>disabled</#if>
                               aria-describedby="<#if messagesPerField.existsError('username')>input-error-username</#if>"/>
                    </div>
                    <#if messagesPerField.existsError('username')>
                        <span id="input-error-username" class="field-error" aria-live="polite">
              ${kcSanitize(messagesPerField.get('username'))?no_esc}
            </span>
                    </#if>
                </div>

                <div class="field">
                    <label for="password">${msg("password")}</label>
                    <div class="input-wrap <#if messagesPerField.existsError('username','password')>has-error</#if>"
                         id="passWrap">
            <span class="input-icon" aria-hidden="true">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                   stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
            </span>
                        <input tabindex="2"
                               id="password"
                               name="password"
                               type="password"
                               autocomplete="current-password"
                               aria-describedby="<#if messagesPerField.existsError('password')>input-error-password</#if>"/>
                        <button type="button"
                                class="btn-eye"
                                id="toggleEye"
                                aria-label="${msg('showPassword')}"
                                onclick="hsgLogin.togglePassword()">
                            <svg id="eyeOpen" width="18" height="18" viewBox="0 0 24 24" fill="none"
                                 stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                                 aria-hidden="true">
                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                <circle cx="12" cy="12" r="3"/>
                            </svg>
                            <svg id="eyeClosed" width="18" height="18" viewBox="0 0 24 24" fill="none"
                                 stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                                 aria-hidden="true" style="display:none">
                                <path d="M17.94 17.94A10.94 10.94 0 0 1 12 19c-7 0-11-7-11-7a19.77 19.77 0 0 1 4.22-5.22"/>
                                <path d="M9.9 4.24A10.94 10.94 0 0 1 12 4c7 0 11 7 11 7a19.84 19.84 0 0 1-3.17 4.19"/>
                                <line x1="1" y1="1" x2="23" y2="23"/>
                            </svg>
                        </button>
                    </div>
                    <#if messagesPerField.existsError('password')>
                        <span id="input-error-password" class="field-error" aria-live="polite">
              ${kcSanitize(messagesPerField.get('password'))?no_esc}
            </span>
                    </#if>
                </div>

                <div class="row-between">
                    <#if realm.rememberMe && !usernameEditDisabled??>
                        <label class="check">
                            <input tabindex="3"
                                   id="rememberMe"
                                   name="rememberMe"
                                   type="checkbox"
                                   <#if login.rememberMe??>checked</#if>/>
                            ${msg("rememberMe")}
                        </label>
                    <#else>
                        <span></span>
                    </#if>

                    <#if realm.resetPasswordAllowed>
                        <a href="${url.loginResetCredentialsUrl}" tabindex="5">
                            ${msg("doForgotPassword")}
                        </a>
                    </#if>
                </div>

                <button tabindex="4"
                        id="kc-login"
                        type="submit"
                        class="btn btn-primary"
                        name="login">
                    <span class="spinner" id="spinner" aria-hidden="true"></span>
                    <span class="btn-label" id="btnLabel">${msg("doLogIn")}</span>
                </button>

            </form>

            <#if realm.registrationAllowed && !registrationDisabled??>
                <div class="divider"><span>${msg("noAccount")}</span></div>
                <a href="${url.registrationUrl}" class="btn btn-secondary" tabindex="6">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                        <circle cx="9" cy="7" r="4"/>
                        <line x1="19" y1="8" x2="19" y2="14"/>
                        <line x1="22" y1="11" x2="16" y2="11"/>
                    </svg>
                    ${msg("doRegister")}
                </a>
            </#if>

            <div class="card-footer">
                <span>&copy; 2026 HSG &middot; ${realm.displayName!'HSG HIS'}</span>
                <#if locale?? && locale.supported?size gt 1>
                    <div class="lang-switch">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <circle cx="12" cy="12" r="10"/>
                            <line x1="2" y1="12" x2="22" y2="12"/>
                            <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/>
                        </svg>
                        <select aria-label="Idioma" onchange="window.location.href=this.value">
                            <#list locale.supported as l>
                            <option value="${l.url}" ${(l.selected!false)?then('selected', '')}>
                                </#list>
                        </select>
                    </div>
                </#if>
            </div>

        </div>

    </section>

</div>

<script src="${url.resourcesPath}/js/login.js"></script>

</body>
</html>
