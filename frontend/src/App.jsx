import { useEffect, useMemo, useState } from "react";
import {
  Navigate,
  NavLink,
  Route,
  Routes,
  useLocation,
  useNavigate,
  useParams,
} from "react-router-dom";
import {
  Activity,
  AlertTriangle,
  Apple,
  BarChart3,
  Bell,
  Camera,
  ChevronRight,
  Clock3,
  FileText,
  HeartPulse,
  Home,
  ImagePlus,
  LogOut,
  Menu,
  PanelLeftClose,
  Pill,
  Plus,
  Settings,
  ShieldCheck,
  Stethoscope,
  Syringe,
  Trash2,
  User,
  Users,
  X,
} from "lucide-react";
import { api, patientId, upload } from "./api";

const ctxLabels = {
  FASTING: "En ayunas",
  BEFORE_MEAL: "Antes de comer",
  AFTER_MEAL: "Después de comer",
  BEDTIME: "Antes de dormir",
  OTHER: "Otro",
};
const mealLabels = {
  BREAKFAST: "Desayuno",
  LUNCH: "Almuerzo",
  DINNER: "Cena",
  SNACK: "Snack",
};
const fmtDate = (v) =>
  new Intl.DateTimeFormat("es-PE", {
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(v));

function App() {
  const [session, setSession] = useState(() =>
    JSON.parse(localStorage.getItem("glucontrol-session") || "null"),
  );
  const save = (s) => {
    setSession(s);
    s
      ? localStorage.setItem("glucontrol-session", JSON.stringify(s))
      : localStorage.removeItem("glucontrol-session");
  };
  return (
    <Routes>
      <Route path="/login" element={<Login onLogin={save} />} />
      <Route path="/register" element={<Register onRegister={save} />} />
      <Route
        path="/doctor/*"
        element={
          session?.role === "DOCTOR" ? (
            <DoctorLayout session={session} logout={() => save(null)} />
          ) : (
            <Navigate to="/login" />
          )
        }
      />
      <Route
        path="/*"
        element={
          session ? (
            <PatientLayout session={session} logout={() => save(null)} />
          ) : (
            <Navigate to="/login" />
          )
        }
      />
    </Routes>
  );
}

function Login({ onLogin }) {
  const nav = useNavigate();
  const [email, setEmail] = useState("paciente@glucontrol.pe");
  const [password, setPassword] = useState("password");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const submit = async (e) => {
    e.preventDefault();
    setBusy(true);
    setError("");
    try {
      const s = await api("/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
      });
      onLogin(s);
      nav(s.role === "DOCTOR" ? "/doctor" : "/");
    } catch (e) {
      setError(e.message);
    } finally {
      setBusy(false);
    }
  };
  return (
    <main className="login-page">
      <section className="login-card">
        <Brand />
        <div className="login-copy">
          <h1>Tu salud, bajo control</h1>
          <p>Conecta tus hábitos, mediciones y tratamiento en un solo lugar.</p>
        </div>
        <form onSubmit={submit} className="stack">
          <label>
            Correo electrónico
            <input
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              type="email"
              required
            />
          </label>
          <label>
            Contraseña
            <input
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              type="password"
              required
            />
          </label>
          {error && <ErrorBox message={error} />}
          <button className="primary" disabled={busy}>
            {busy ? "Ingresando…" : "Iniciar sesión"}
          </button>
        </form>
        <p className="auth-switch">
          ¿No tienes cuenta? <NavLink to="/register">Regístrate</NavLink>
        </p>
        <div className="divider">
          <span>o usa una cuenta demo</span>
        </div>
        <div className="demo-actions">
          <button
            className="secondary"
            onClick={() => {
              setEmail("paciente@glucontrol.pe");
              setPassword("password");
            }}
          >
            Paciente
          </button>
          <button
            className="secondary"
            onClick={() => {
              setEmail("medico@glucontrol.pe");
              setPassword("password");
            }}
          >
            Médico
          </button>
        </div>
        <p className="privacy">
          <ShieldCheck size={15} /> Tus datos de salud están protegidos
        </p>
      </section>
    </main>
  );
}

function Register({ onRegister }) {
  const nav = useNavigate();
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    birthDate: "",
    phone: "",
    diabetesType: "",
  });
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const change = (field) => (event) => setForm({ ...form, [field]: event.target.value });
  const submit = async (event) => {
    event.preventDefault();
    setBusy(true);
    setError("");
    try {
      const session = await api("/auth/register", {
        method: "POST",
        body: JSON.stringify({ ...form, birthDate: form.birthDate || null }),
      });
      onRegister(session);
      nav("/");
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setBusy(false);
    }
  };
  return (
    <main className="login-page register-page">
      <section className="login-card register-card">
        <Brand />
        <div className="login-copy">
          <h1>Crea tu cuenta</h1>
          <p>Empieza a registrar tu salud y comparte resultados con tu médico.</p>
        </div>
        <form onSubmit={submit} className="stack">
          <div className="two-cols">
            <label>Nombres<input value={form.firstName} onChange={change("firstName")} required /></label>
            <label>Apellidos<input value={form.lastName} onChange={change("lastName")} required /></label>
          </div>
          <label>Correo electrónico<input value={form.email} onChange={change("email")} type="email" required /></label>
          <label>Contraseña<input value={form.password} onChange={change("password")} type="password" minLength="8" required /><small>Mínimo 8 caracteres</small></label>
          <div className="two-cols">
            <label>Fecha de nacimiento<input value={form.birthDate} onChange={change("birthDate")} type="date" /></label>
            <label>Teléfono (opcional)<input value={form.phone} onChange={change("phone")} type="tel" /></label>
          </div>
          <label>Tipo de diabetes (opcional)<select value={form.diabetesType} onChange={change("diabetesType")}><option value="">Por definir</option><option value="Tipo 1">Tipo 1</option><option value="Tipo 2">Tipo 2</option><option value="Gestacional">Gestacional</option></select></label>
          {error && <ErrorBox message={error} />}
          <button className="primary" disabled={busy}>{busy ? "Creando cuenta…" : "Crear mi cuenta"}</button>
        </form>
        <p className="auth-switch">¿Ya tienes cuenta? <NavLink to="/login">Inicia sesión</NavLink></p>
        <p className="privacy"><ShieldCheck size={15} /> Tus datos se almacenan de forma segura</p>
      </section>
    </main>
  );
}
function Brand() {
  return (
    <div className="brand">
      <span className="brand-mark">
        <Activity />
      </span>
      <span>GluControl</span>
    </div>
  );
}
const patientNav = [
  ["/", Home, "Inicio"],
  ["/glucose", Activity, "Glucosa"],
  ["/meals", Apple, "Alimentos"],
  ["/medications", Pill, "Medicinas"],
  ["/history", Clock3, "Historial"],
];
const patientDesktopNav = [
  ["/", Home, "Resumen"],
  ["/glucose", Activity, "Glucosa"],
  ["/meals", Apple, "Alimentación"],
  ["/medications", Pill, "Medicamentos"],
  ["/history", Clock3, "Historial"],
  ["/alerts", Bell, "Alertas"],
  ["/reports", FileText, "Reportes"],
  ["/profile", User, "Mi perfil"],
];
function PatientDesktopSidebar({ session, logout }) {
  const initials = session.fullName.split(" ").map((part) => part[0]).slice(0, 2).join("");
  return (
    <aside className="patient-desktop-sidebar">
      <Brand />
      <div className="patient-desktop-profile">
        <div className="avatar">{initials}</div>
        <div><strong>{session.fullName}</strong><small>Cuenta paciente</small></div>
      </div>
      <nav>{patientDesktopNav.map(([to, Icon, label]) => (
        <NavLink key={to} to={to} end={to === "/"}><Icon size={19} /><span>{label}</span></NavLink>
      ))}</nav>
      <button className="logout" onClick={logout}><LogOut size={18} />Cerrar sesión</button>
    </aside>
  );
}
function PatientLayout({ session, logout }) {
  const [menu, setMenu] = useState(false);
  const loc = useLocation();
  useEffect(() => setMenu(false), [loc.pathname]);
  return (
    <div className="patient-shell">
      <PatientDesktopSidebar session={session} logout={logout} />
      <header className="mobile-top">
        <Brand />
        <button
          className="icon-btn"
          onClick={() => setMenu(true)}
          aria-label="Abrir menú"
        >
          <Menu />
        </button>
      </header>
      {menu && (
        <>
          <button className="scrim" onClick={() => setMenu(false)} aria-label="Cerrar menú" />
          <aside className="drawer">
            <button className="drawer-close" onClick={() => setMenu(false)} aria-label="Cerrar menú lateral">
              <X />
            </button>
            <div className="profile-mini">
              <div className="avatar">JM</div>
              <strong>{session.fullName}</strong>
              <small>Paciente · Diabetes tipo 2</small>
            </div>
            <NavLink to="/profile" className="primary mini">
              Ver mi perfil
            </NavLink>
            <nav>
              {[
                ["/", Home, "Resumen de salud"],
                ["/glucose", Activity, "Registro de glucosa"],
                ["/medications", Pill, "Medicamentos"],
                ["/meals", Apple, "Registro de alimentación"],
                ["/history", Clock3, "Historial médico"],
                ["/alerts", Bell, "Alertas inteligentes"],
                ["/reports", FileText, "Generar reporte"],
                ["/profile", User, "Perfil y ajustes"],
              ].map(([to, I, l]) => (
                <NavLink key={to} to={to}>
                  <I size={19} />
                  {l}
                </NavLink>
              ))}
            </nav>
            <button className="logout" onClick={logout}>
              <LogOut size={18} />
              Cerrar sesión
            </button>
          </aside>
        </>
      )}
      <div className="phone-content">
        <Routes>
          <Route index element={<Dashboard session={session} />} />
          <Route path="glucose" element={<Glucose />} />
          <Route path="meals" element={<Meals />} />
          <Route path="medications" element={<Medications />} />
          <Route path="history" element={<History />} />
          <Route path="alerts" element={<Alerts />} />
          <Route path="reports" element={<Reports />} />
          <Route path="profile" element={<Profile session={session} />} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </div>
      <nav className="bottom-nav">
        {patientNav.map(([to, I, l]) => (
          <NavLink key={to} to={to} end={to === "/"}>
            <I />
            <span>{l}</span>
          </NavLink>
        ))}
      </nav>
    </div>
  );
}

function useLoad(loader, deps = []) {
  const [state, set] = useState({ loading: true, data: null, error: "" });
  const load = () => {
    set((s) => ({ ...s, loading: true, error: "" }));
    loader()
      .then((data) => set({ loading: false, data, error: "" }))
      .catch((e) => set({ loading: false, data: null, error: e.message }));
  };
  useEffect(load, deps);
  return { ...state, reload: load };
}
function PageState({ loading, error, children }) {
  if (loading)
    return (
      <div className="state">
        <span className="spinner" />
        Cargando tus datos…
      </div>
    );
  if (error) return <ErrorBox message={error} />;
  return children;
}
function ErrorBox({ message }) {
  return (
    <div className="error-box">
      <AlertTriangle size={18} />
      {message}
    </div>
  );
}
function Header({ title, subtitle, action }) {
  return (
    <div className="page-header">
      <div>
        <p className="eyebrow">GluControl</p>
        <h1>{title}</h1>
        {subtitle && <p>{subtitle}</p>}
      </div>
      {action}
    </div>
  );
}

function Dashboard({ session }) {
  const g = useLoad(() => api(`/patients/${patientId()}/glucose?size=4`), []);
  const m = useLoad(() => api(`/patients/${patientId()}/medications`), []);
  const last = g.data?.content?.[0];
  return (
    <div className="patient-page dashboard-page">
      <Header
        title={`Hola, ${session.fullName.split(" ")[0]}`}
        subtitle="Así va tu salud hoy"
      />
      <PageState loading={g.loading || m.loading} error={g.error || m.error}>
        <section className="hero-reading">
          <div>
            <small>Tu última glucosa</small>
            <strong>
              {last?.valueMgDl || "—"} <span>mg/dL</span>
            </strong>
            <p className={last ? "good" : "reading-empty"}>
              <ShieldCheck size={16} /> {last ? (last.rangeStatus === "IN_RANGE" ? "Dentro de tu rango objetivo" : "Lectura fuera del rango objetivo") : "Aún no hay mediciones"}
            </p>
          </div>
          <span className="pulse">
            <Activity />
          </span>
          <NavLink to="/glucose" className="primary">
            + Registrar glucosa
          </NavLink>
        </section>
        <div className="quick-grid">
          <article>
            <span className="stat-icon pink">
              <Activity />
            </span>
            <small>Mediciones</small>
            <strong>{g.data?.totalElements || 0}</strong>
            <em>Registros totales</em>
          </article>
          <article>
            <span className="stat-icon green">
              <Pill />
            </span>
            <small>Medicamentos</small>
            <strong>{m.data?.filter((item) => item.active).length || 0}</strong>
            <em>Tratamientos activos</em>
          </article>
        </div>
        <SectionTitle title="Próximos medicamentos" to="/medications" />
        <div className="list-card">
          {m.data?.slice(0, 2).map((x) => (
            <div className="list-row" key={x.id}>
              <span className="med-icon">
                <Pill />
              </span>
              <div>
                <strong>{x.name}</strong>
                <small>
                  {x.dose} · {x.frequency}
                </small>
              </div>
              <time>{x.reminderTime?.slice(0, 5)}</time>
            </div>
          ))}
        </div>
        <article className="insight">
          <div>
            <p className="eyebrow">Consejo del día</p>
            <strong>
              Caminar después de comer ayuda a estabilizar tu glucosa
            </strong>
          </div>
          <Activity />
        </article>
      </PageState>
    </div>
  );
}
function SectionTitle({ title, to }) {
  return (
    <div className="section-title">
      <h2>{title}</h2>
      {to && (
        <NavLink to={to}>
          Ver todo <ChevronRight size={16} />
        </NavLink>
      )}
    </div>
  );
}

function Glucose() {
  const readings = useLoad(
    () => api(`/patients/${patientId()}/glucose?size=20`),
    [],
  );
  const [open, setOpen] = useState(false);
  const [value, setValue] = useState("");
  const [context, setContext] = useState("AFTER_MEAL");
  const [saved, setSaved] = useState(false);
  const save = async (e) => {
    e.preventDefault();
    await api("/glucose", {
      method: "POST",
      body: JSON.stringify({
        patientId: patientId(),
        valueMgDl: Number(value),
        measuredAt: new Date().toISOString(),
        context,
      }),
    });
    setOpen(false);
    setSaved(true);
    setValue("");
    readings.reload();
  };
  return (
    <>
      <Header
        title="Registro de glucosa"
        subtitle="Monitorea tus niveles y descubre tendencias"
        action={
          <button className="primary circle" onClick={() => setOpen(!open)} aria-label="Agregar medición">
            <Plus />
          </button>
        }
      />
      {saved && (
        <div className="success">
          <ShieldCheck />
          ¡Buen trabajo! Tu medición fue guardada.
        </div>
      )}
      {open && (
        <form className="form-card" onSubmit={save}>
          <label>
            Valor de glucosa
            <div className="unit-input">
              <input
                autoFocus
                type="number"
                min="20"
                max="600"
                value={value}
                onChange={(e) => setValue(e.target.value)}
                required
              />
              <span>mg/dL</span>
            </div>
          </label>
          <label>
            ¿Cuándo realizaste la medición?
            <select
              value={context}
              onChange={(e) => setContext(e.target.value)}
            >
              {Object.entries(ctxLabels).map(([v, l]) => (
                <option value={v} key={v}>
                  {l}
                </option>
              ))}
            </select>
          </label>
          <button className="primary">Guardar registro</button>
        </form>
      )}
      <PageState loading={readings.loading} error={readings.error}>
        <article className="chart-card">
          <div className="range-heading">
            <span>Últimos 7 registros</span>
            <em>Rango 70–180</em>
          </div>
          <div className="line-chart">
            {[96, 112, 104, 126, 99, 118, 105].map((v, i) => (
              <i key={i} style={{ height: `${v / 1.8}px` }}>
                <span>{v}</span>
              </i>
            ))}
          </div>
        </article>
        <SectionTitle title="Historial reciente" />
        <div className="list-card">
          {readings.data?.content?.map((x) => (
            <div className="list-row reading" key={x.id}>
              <span className={`dot ${x.rangeStatus.toLowerCase()}`} />
              <div>
                <strong>
                  {x.valueMgDl} <small>mg/dL</small>
                </strong>
                <small>
                  {ctxLabels[x.context]} · {fmtDate(x.measuredAt)}
                </small>
              </div>
              <em
                className={
                  x.rangeStatus === "IN_RANGE" ? "tag good-tag" : "tag warn-tag"
                }
              >
                {x.rangeStatus === "IN_RANGE" ? "En rango" : "Revisar"}
              </em>
            </div>
          ))}
        </div>
      </PageState>
    </>
  );
}

function Meals() {
  const data = useLoad(() => api(`/patients/${patientId()}/meals?size=20`), []);
  const [open, setOpen] = useState(false);
  const [imageFile, setImageFile] = useState(null);
  const [imageError, setImageError] = useState("");
  const [saving, setSaving] = useState(false);
  const imagePreview = useMemo(() => imageFile ? URL.createObjectURL(imageFile) : "", [imageFile]);
  useEffect(() => () => { if (imagePreview) URL.revokeObjectURL(imagePreview); }, [imagePreview]);
  const [form, setForm] = useState({
    name: "",
    mealType: "LUNCH",
    carbohydratesGrams: 30,
    calories: 400,
  });
  const chooseImage = (event) => {
    const file = event.target.files?.[0];
    if (!file) return;
    if (!file.type.startsWith("image/")) return setImageError("Selecciona un archivo de imagen");
    if (file.size > 5 * 1024 * 1024) return setImageError("La imagen supera el máximo de 5 MB");
    setImageError("");
    setImageFile(file);
  };
  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    setImageError("");
    try {
      const photo = imageFile ? await upload("/uploads/meals", imageFile) : null;
      await api("/meals", {
        method: "POST",
        body: JSON.stringify({ ...form, patientId: patientId(), eatenAt: new Date().toISOString(), photoUrl: photo?.url || null }),
      });
      setOpen(false);
      setImageFile(null);
      data.reload();
    } catch (requestError) {
      setImageError(requestError.message);
    } finally {
      setSaving(false);
    }
  };
  return (
    <div className="patient-page">
      <Header
        title="Alimentación"
        subtitle="Registra lo que comes y cuida tu equilibrio"
        action={
          <button className="primary circle" onClick={() => setOpen(!open)} aria-label="Agregar comida">
            <Plus />
          </button>
        }
      />
      {open && (
        <form className="form-card" onSubmit={save}>
          <label>
            Nombre del plato
            <input
              required
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              placeholder="Ej. pollo con ensalada"
            />
          </label>
          <label>
            Momento
            <select
              value={form.mealType}
              onChange={(e) => setForm({ ...form, mealType: e.target.value })}
            >
              {Object.entries(mealLabels).map(([v, l]) => (
                <option value={v} key={v}>
                  {l}
                </option>
              ))}
            </select>
          </label>
          <div className="two-cols">
            <label>
              Carbohidratos
              <input
                type="number"
                value={form.carbohydratesGrams}
                onChange={(e) =>
                  setForm({
                    ...form,
                    carbohydratesGrams: Number(e.target.value),
                  })
                }
              />
            </label>
            <label>
              Calorías
              <input
                type="number"
                value={form.calories}
                onChange={(e) =>
                  setForm({ ...form, calories: Number(e.target.value) })
                }
              />
            </label>
          </div>
          <div className="photo-upload">
            <strong>Foto del plato</strong>
            <p>Opcional · JPEG, PNG o WebP · máximo 5 MB</p>
            <div className="photo-actions">
              <label className="secondary upload-button" htmlFor="meal-camera"><Camera size={18} /> Abrir cámara</label>
              <input id="meal-camera" className="visually-hidden" type="file" accept="image/*" capture="environment" onChange={chooseImage} />
              <label className="secondary upload-button" htmlFor="meal-file"><ImagePlus size={18} /> Subir imagen</label>
              <input id="meal-file" className="visually-hidden" type="file" accept="image/jpeg,image/png,image/webp" onChange={chooseImage} />
            </div>
            {imagePreview && <div className="photo-preview"><img src={imagePreview} alt="Vista previa del plato" /><button type="button" onClick={() => setImageFile(null)} aria-label="Eliminar imagen"><Trash2 size={18} /></button></div>}
            {imageError && <ErrorBox message={imageError} />}
          </div>
          <button className="primary" disabled={saving}>{saving ? "Guardando…" : "Guardar alimento"}</button>
        </form>
      )}
      <PageState loading={data.loading} error={data.error}>
        <div className="meal-hero">
          <Apple />
          <div>
            <small>Balance de hoy</small>
            <strong>¡Vas por buen camino!</strong>
            <p>2 de 3 comidas registradas</p>
          </div>
        </div>
        <SectionTitle title="Comidas recientes" />
        <div className="meal-list">
          {data.data?.content?.map((x) => (
            <article key={x.id}>
              <div className="food-thumb">{x.photoUrl ? <img src={x.photoUrl} alt={x.name} /> : <Apple />}</div>
              <div>
                <em>{mealLabels[x.mealType]}</em>
                <h3>{x.name}</h3>
                <p>
                  {x.carbohydratesGrams} g carbohidratos · {x.calories} kcal
                </p>
                <small>{fmtDate(x.eatenAt)}</small>
              </div>
            </article>
          ))}
        </div>
      </PageState>
    </div>
  );
}

function Medications() {
  const data = useLoad(() => api(`/patients/${patientId()}/medications`), []);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({
    name: "",
    dose: "",
    frequency: "1 vez al día",
    reminderTime: "08:00",
    active: true,
  });
  const save = async (e) => {
    e.preventDefault();
    await api("/medications", {
      method: "POST",
      body: JSON.stringify({
        ...form,
        patientId: patientId(),
        startDate: new Date().toISOString().slice(0, 10),
      }),
    });
    setOpen(false);
    data.reload();
  };
  return (
    <>
      <Header
        title="Mis medicamentos"
        subtitle="Tu tratamiento, siempre a tiempo"
        action={
          <button className="primary circle" onClick={() => setOpen(!open)} aria-label="Agregar medicamento">
            <Plus />
          </button>
        }
      />
      {open && (
        <form className="form-card" onSubmit={save}>
          <label>
            Medicamento
            <input
              required
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
            />
          </label>
          <label>
            Dosis
            <input
              required
              value={form.dose}
              onChange={(e) => setForm({ ...form, dose: e.target.value })}
              placeholder="Ej. 850 mg"
            />
          </label>
          <div className="two-cols">
            <label>
              Frecuencia
              <input
                value={form.frequency}
                onChange={(e) =>
                  setForm({ ...form, frequency: e.target.value })
                }
              />
            </label>
            <label>
              Hora
              <input
                type="time"
                value={form.reminderTime}
                onChange={(e) =>
                  setForm({ ...form, reminderTime: e.target.value })
                }
              />
            </label>
          </div>
          <button className="primary">Agregar medicamento</button>
        </form>
      )}
      <PageState loading={data.loading} error={data.error}>
        <div className="adherence">
          <div className="ring">
            92<small>%</small>
          </div>
          <div>
            <strong>Excelente adherencia</strong>
            <p>Has completado 12 de 13 tomas esta semana</p>
          </div>
        </div>
        <SectionTitle title="Tratamiento activo" />
        <div className="med-cards">
          {data.data?.map((x, i) => (
            <article key={x.id}>
              <div className={`med-icon large color-${i % 3}`}>
                <Pill />
              </div>
              <div>
                <h3>{x.name}</h3>
                <p>
                  {x.dose} · {x.frequency}
                </p>
                <span>
                  <Clock3 /> Próxima toma: {x.reminderTime?.slice(0, 5)}
                </span>
              </div>
              <button className="secondary tiny">Tomado</button>
            </article>
          ))}
        </div>
      </PageState>
    </>
  );
}

function History() {
  const g = useLoad(() => api(`/patients/${patientId()}/glucose?size=30`), []);
  const m = useLoad(() => api(`/patients/${patientId()}/meals?size=30`), []);
  const events = useMemo(
    () =>
      [
        ...(g.data?.content || []).map((x) => ({
          ...x,
          kind: "glucose",
          date: x.measuredAt,
        })),
        ...(m.data?.content || []).map((x) => ({
          ...x,
          kind: "meal",
          date: x.eatenAt,
        })),
      ].sort((a, b) => new Date(b.date) - new Date(a.date)),
    [g.data, m.data],
  );
  return (
    <>
      <Header
        title="Historial de salud"
        subtitle="Todo tu progreso en una línea de tiempo"
      />
      <PageState loading={g.loading || m.loading} error={g.error || m.error}>
        <div className="history-summary">
          <div>
            <small>Promedio 7 días</small>
            <strong>
              108 <i>mg/dL</i>
            </strong>
          </div>
          <div>
            <small>Lecturas en rango</small>
            <strong>92%</strong>
          </div>
        </div>
        <div className="timeline">
          {events.map((e, i) => (
            <article key={`${e.kind}-${e.id}-${i}`}>
              <span className={e.kind}>
                <span>{e.kind === "glucose" ? <Activity /> : <Apple />}</span>
              </span>
              <div>
                <time>{fmtDate(e.date)}</time>
                <strong>
                  {e.kind === "glucose" ? `${e.valueMgDl} mg/dL` : e.name}
                </strong>
                <p>
                  {e.kind === "glucose"
                    ? ctxLabels[e.context]
                    : `${mealLabels[e.mealType]} · ${e.carbohydratesGrams} g carbohidratos`}
                </p>
              </div>
            </article>
          ))}
        </div>
      </PageState>
    </>
  );
}

function Alerts() {
  const data = useLoad(
    () => api(`/patients/${patientId()}/alerts?size=30`),
    [],
  );
  const ack = async (id) => {
    await api(`/alerts/${id}/acknowledge`, { method: "PATCH" });
    data.reload();
  };
  return (
    <>
      <Header
        title="Alertas inteligentes"
        subtitle="Avisos importantes para cuidar de ti"
      />
      <PageState loading={data.loading} error={data.error}>
        <div className="alert-list">
          {data.data?.content?.length ? (
            data.data.content.map((x) => (
              <article
                className={`alert-card ${x.severity.toLowerCase()}`}
                key={x.id}
              >
                <span>
                  <Bell />
                </span>
                <div>
                  <em>
                    {x.severity === "CRITICAL"
                      ? "Atención inmediata"
                      : x.severity === "WARNING"
                        ? "Recomendación"
                        : "Recordatorio"}
                  </em>
                  <h3>{x.title}</h3>
                  <p>{x.message}</p>
                  <small>{fmtDate(x.occurredAt)}</small>
                </div>
                {!x.acknowledged && (
                  <button onClick={() => ack(x.id)}>Entendido</button>
                )}
              </article>
            ))
          ) : (
            <Empty icon={<Bell />} text="No tienes alertas pendientes" />
          )}
        </div>
      </PageState>
    </>
  );
}
function Reports() {
  const data = useLoad(() => api(`/patients/${patientId()}/reports`), []);
  return (
    <>
      <Header
        title="Reporte para tu médico"
        subtitle="Un resumen claro de tu evolución"
      />
      <PageState loading={data.loading} error={data.error}>
        <article className="report-card">
          <div className="report-top">
            <Brand />
            <span>Últimos 30 días</span>
          </div>
          <h2>Resumen de salud</h2>
          <div className="report-kpis">
            <div>
              <strong>108</strong>
              <small>Glucosa promedio</small>
            </div>
            <div>
              <strong>92%</strong>
              <small>Tiempo en rango</small>
            </div>
            <div>
              <strong>24.2</strong>
              <small>IMC</small>
            </div>
          </div>
          <div className="report-bars">
            {[45, 60, 50, 72, 66, 80, 58, 76, 68, 84, 72, 92].map((v, i) => (
              <i style={{ height: v }} key={i} />
            ))}
          </div>
          <div className="doctor-note">
            <Stethoscope />
            <p>
              {data.data?.[0]?.summary ||
                "Tus indicadores muestran una evolución favorable."}
            </p>
          </div>
        </article>
        <button className="primary full">
          <FileText /> Descargar reporte PDF
        </button>
        <button className="secondary full">Compartir con mi médico</button>
      </PageState>
    </>
  );
}
function Profile({ session }) {
  const p = useLoad(() => api(`/patients/${patientId()}`), []);
  return (
    <>
      <Header title="Mi perfil" subtitle="Datos personales y preferencias" />
      <PageState loading={p.loading} error={p.error}>
        <div className="profile-card">
          <div className="avatar big">JM</div>
          <h2>{session.fullName}</h2>
          <p>{session.email}</p>
          <span>Paciente verificado</span>
        </div>
        <SectionTitle title="Información médica" />
        <div className="details-card">
          <p>
            <span>Tipo de diabetes</span>
            <strong>{p.data?.diabetesType}</strong>
          </p>
          <p>
            <span>Rango objetivo</span>
            <strong>
              {p.data?.glucoseTargetMin}–{p.data?.glucoseTargetMax} mg/dL
            </strong>
          </p>
          <p>
            <span>Contacto</span>
            <strong>{p.data?.phone}</strong>
          </p>
          <p>
            <span>Emergencia</span>
            <strong>{p.data?.emergencyContact}</strong>
          </p>
        </div>
        <SectionTitle title="Preferencias" />
        <div className="details-card">
          <p>
            <span>Alertas de glucosa</span>
            <strong className="switch on" />
          </p>
          <p>
            <span>Recordatorios de medicación</span>
            <strong className="switch on" />
          </p>
          <p>
            <span>Recordatorios de comida</span>
            <strong className="switch on" />
          </p>
        </div>
      </PageState>
    </>
  );
}
function Empty({ icon, text }) {
  return (
    <div className="empty">
      {icon}
      <strong>{text}</strong>
      <p>Cuando haya novedades aparecerán aquí.</p>
    </div>
  );
}

function DoctorLayout({ session, logout }) {
  const [collapsed, setCollapsed] = useState(false);
  return (
    <div className={`doctor-shell ${collapsed ? "collapsed" : ""}`}>
      <aside className="doctor-sidebar">
        <Brand />
        <button className="collapse" onClick={() => setCollapsed(!collapsed)} aria-label="Contraer navegación">
          <PanelLeftClose />
        </button>
        <nav>
          <NavLink to="/doctor" aria-label="Panel médico">
            <BarChart3 /> <span>Panel</span>
          </NavLink>
          <NavLink to="/doctor/patients" aria-label="Pacientes">
            <Users /> <span>Pacientes</span>
          </NavLink>
          <NavLink to="/doctor/alerts" aria-label="Alertas">
            <Bell /> <span>Alertas</span>
          </NavLink>
          <NavLink to="/doctor/reports" aria-label="Informes">
            <FileText /> <span>Informes</span>
          </NavLink>
          <NavLink to="/doctor/settings" aria-label="Configuración">
            <Settings /> <span>Configuración</span>
          </NavLink>
        </nav>
        <button className="logout" onClick={logout}>
          <LogOut />
          <span>Cerrar sesión</span>
        </button>
      </aside>
      <main className="doctor-main">
        <header>
          <div>
            <p>Panel de control médico</p>
            <strong>{session.fullName}</strong>
          </div>
          <span className="doctor-badge">
            <Stethoscope /> Endocrinología
          </span>
        </header>
        <Routes>
          <Route index element={<DoctorDashboard />} />
          <Route path="patients" element={<DoctorPatients />} />
          <Route path="patients/:patientId" element={<DoctorPatientDetail />} />
          <Route path="alerts" element={<DoctorAlerts />} />
          <Route path="reports" element={<DoctorReports />} />
          <Route path="settings" element={<DoctorSettings />} />
        </Routes>
      </main>
    </div>
  );
}
function DoctorDashboard() {
  const d = useLoad(() => api("/doctor/dashboard"), []);
  return (
    <PageState loading={d.loading} error={d.error}>
      <div className="desktop-title">
        <div>
          <p className="eyebrow">Visión general</p>
          <h1>Buenos días, doctora</h1>
          <p>Este es el estado de su clínica hoy.</p>
        </div>
      </div>
      <div className="kpi-grid">
        <Kpi
          icon={<Users />}
          label="Pacientes activos"
          value={d.data?.activePatients}
          trend="+2.4%"
        />
        <Kpi
          icon={<AlertTriangle />}
          label="Alertas críticas"
          value={d.data?.criticalAlerts}
          red
        />
        <Kpi
          icon={<Clock3 />}
          label="Alertas pendientes"
          value={d.data?.pendingAlerts}
        />
        <Kpi
          icon={<Activity />}
          label="Glucosa promedio"
          value={d.data?.averageGlucose ? `${d.data.averageGlucose} mg/dL` : "—"}
          trend="Datos registrados"
        />
      </div>
      <div className="doctor-grid">
        <article className="analytics-card">
          <div className="section-title">
            <h2>Estabilidad de glucosa</h2>
            <em>Últimos 7 días</em>
          </div>
          <div className="desktop-chart">
            {(d.data?.patients || []).map((patient) => (
              <i key={patient.id} title={patient.fullName} style={{ height: `${Math.max(24, Math.min(180, patient.lastGlucose || 24))}px` }} />
            ))}
          </div>
        </article>
        <article className="priority-card">
          <SectionTitle title="Alertas de prioridad" to="/doctor/alerts" />
          {(d.data?.patients || []).filter((patient) => patient.status !== "ESTABLE").slice(0, 4).map((patient) => (
            <NavLink to={`/doctor/patients/${patient.id}`} className="priority-item" key={patient.id}>
              <span className={patient.status === "ATENCION" ? "red-dot" : "amber-dot"} />
              <div><strong>{patient.fullName}</strong><p>{patient.lastGlucose ? `${patient.lastGlucose} mg/dL` : "Sin mediciones"}</p></div>
              <ChevronRight size={16} />
            </NavLink>
          ))}
          {!d.data?.patients?.some((patient) => patient.status !== "ESTABLE") && <Empty icon={<ShieldCheck />} text="Sin pacientes que requieran atención" />}
        </article>
      </div>
      <DoctorTable rows={d.data?.patients || []} />
    </PageState>
  );
}
function Kpi({ icon, label, value, trend, red }) {
  return (
    <article className={`kpi ${red ? "danger" : ""}`}>
      <span>{icon}</span>
      <div>
        <small>{label}</small>
        <strong>{value}</strong>
        <em>{trend}</em>
      </div>
    </article>
  );
}
function DoctorTable({ rows }) {
  return (
    <article className="table-card">
      <SectionTitle title="Pacientes registrados" to="/doctor/patients" />
      {!rows.length ? <Empty icon={<Users />} text="Aún no hay pacientes registrados" /> :
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Paciente</th>
              <th>Edad</th>
              <th>Última glucosa</th>
              <th>Estado</th>
              <th>Último registro</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => (
              <tr key={r.id}>
                <td>
                  <span className="table-avatar">
                    {r.fullName
                      .split(" ")
                      .map((x) => x[0])
                      .slice(0, 2)}
                  </span>
                  <div><strong>{r.fullName}</strong><small>{r.email}</small></div>
                </td>
                <td>{r.age ?? "—"}</td>
                <td>{r.lastGlucose ? `${r.lastGlucose} mg/dL` : "Sin datos"}</td>
                <td>
                  <em
                    className={`status ${r.status === "ESTABLE" ? "ok" : "attention"}`}
                  >
                    {r.status}
                  </em>
                </td>
                <td>{r.lastClinicalAt ? fmtDate(r.lastClinicalAt) : "Sin actividad"}</td>
                <td><NavLink className="row-action" to={`/doctor/patients/${r.id}`} aria-label={`Ver resultados de ${r.fullName}`}><ChevronRight /></NavLink></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>}
    </article>
  );
}
function DoctorPatients() {
  const p = useLoad(() => api("/doctor/patients?size=100"), []);
  const [q, setQ] = useState("");
  const rows = (p.data?.content || []).filter((x) =>
    `${x.fullName} ${x.email}`.toLowerCase().includes(q.toLowerCase()),
  );
  return (
    <>
      <div className="desktop-title">
        <div>
          <p className="eyebrow">Gestión clínica</p>
          <h1>Pacientes</h1>
          <p>Busca y revisa su estado de salud.</p>
        </div>
      </div>
      <input
        className="search"
        placeholder="Buscar paciente…"
        value={q}
        onChange={(e) => setQ(e.target.value)}
      />
      <PageState loading={p.loading} error={p.error}>
        <DoctorTable rows={rows} />
      </PageState>
    </>
  );
}
function DoctorPatientDetail() {
  const { patientId: selectedPatientId } = useParams();
  const detail = useLoad(async () => {
    const [summary, measurements, mealsData, medicationsData, alertsData] = await Promise.all([
      api(`/doctor/patients/${selectedPatientId}/summary`),
      api(`/doctor/patients/${selectedPatientId}/measurements?size=30`),
      api(`/doctor/patients/${selectedPatientId}/meals?size=20`),
      api(`/doctor/patients/${selectedPatientId}/medications`),
      api(`/doctor/patients/${selectedPatientId}/alerts?size=20`),
    ]);
    return { summary, measurements, meals: mealsData, medications: medicationsData, alerts: alertsData };
  }, [selectedPatientId]);
  return (
    <PageState loading={detail.loading} error={detail.error}>
      <div className="desktop-title doctor-detail-title">
        <div><NavLink className="back-link" to="/doctor/patients">← Volver a pacientes</NavLink><p className="eyebrow">Ficha clínica</p><h1>{detail.data?.summary.patient.fullName}</h1><p>{detail.data?.summary.patient.email} · {detail.data?.summary.patient.diabetesType || "Diagnóstico por definir"}</p></div>
      </div>
      <div className="kpi-grid doctor-detail-kpis">
        <Kpi icon={<Activity />} label="Glucosa promedio" value={detail.data?.summary.averageGlucose ? `${detail.data.summary.averageGlucose} mg/dL` : "—"} />
        <Kpi icon={<ShieldCheck />} label="Lecturas en rango" value={detail.data?.summary.readingsInRangePercent != null ? `${detail.data.summary.readingsInRangePercent}%` : "—"} />
        <Kpi icon={<Pill />} label="Medicamentos activos" value={detail.data?.summary.activeMedications} />
        <Kpi icon={<Bell />} label="Alertas pendientes" value={detail.data?.summary.pendingAlerts} red={detail.data?.summary.pendingAlerts > 0} />
      </div>
      <div className="doctor-detail-grid">
        <article className="table-card"><SectionTitle title="Historial de glucosa" />
          {(detail.data?.measurements.content || []).length ? <div className="clinical-list">{detail.data.measurements.content.map((item) => <div className="list-row reading" key={item.id}><span className={`dot ${item.rangeStatus.toLowerCase()}`} /><div><strong>{item.valueMgDl} <small>mg/dL</small></strong><small>{ctxLabels[item.context]} · {fmtDate(item.measuredAt)}</small></div><em className={item.rangeStatus === "IN_RANGE" ? "tag good-tag" : "tag warn-tag"}>{item.rangeStatus === "IN_RANGE" ? "En rango" : "Revisar"}</em></div>)}</div> : <Empty icon={<Activity />} text="Sin mediciones registradas" />}
        </article>
        <article className="table-card"><SectionTitle title="Comidas" />
          {(detail.data?.meals.content || []).length ? <div className="clinical-list">{detail.data.meals.content.map((item) => <div className="list-row" key={item.id}><div className="doctor-meal-thumb">{item.photoUrl ? <img src={item.photoUrl} alt="" /> : <Apple />}</div><div><strong>{item.name}</strong><small>{mealLabels[item.mealType]} · {fmtDate(item.eatenAt)}</small></div></div>)}</div> : <Empty icon={<Apple />} text="Sin comidas registradas" />}
        </article>
        <article className="table-card"><SectionTitle title="Medicamentos" />
          {detail.data?.medications.length ? <div className="clinical-list">{detail.data.medications.map((item) => <div className="list-row" key={item.id}><span className="med-icon"><Pill /></span><div><strong>{item.name}</strong><small>{item.dose} · {item.frequency}</small></div><em className="tag good-tag">{item.active ? "Activo" : "Finalizado"}</em></div>)}</div> : <Empty icon={<Pill />} text="Sin medicamentos registrados" />}
        </article>
        <article className="table-card"><SectionTitle title="Alertas" />
          {(detail.data?.alerts.content || []).length ? <div className="clinical-list">{detail.data.alerts.content.map((item) => <div className="list-row" key={item.id}><span className="med-icon"><Bell /></span><div><strong>{item.title}</strong><small>{item.message}</small></div><em className={item.severity === "CRITICAL" ? "tag warn-tag" : "tag good-tag"}>{item.severity}</em></div>)}</div> : <Empty icon={<Bell />} text="Sin alertas registradas" />}
        </article>
      </div>
    </PageState>
  );
}
function DoctorAlerts() {
  const d = useLoad(() => api("/doctor/dashboard"), []);
  return (
    <>
      <div className="desktop-title">
        <div>
          <p className="eyebrow">Centro de alertas</p>
          <h1>Alertas clínicas</h1>
          <p>Prioriza los casos que necesitan atención.</p>
        </div>
      </div>
      <div className="wide-empty">
        <Bell />
        <h2>{d.data?.criticalAlerts || 0} alertas sin revisar</h2>
        <p>
          Abre la ficha de cada paciente desde el panel para realizar
          seguimiento.
        </p>
      </div>
    </>
  );
}
function DoctorReports() {
  return (
    <>
      <div className="desktop-title">
        <div>
          <p className="eyebrow">Análisis</p>
          <h1>Informes médicos</h1>
          <p>Genera reportes clínicos para tus pacientes.</p>
        </div>
        <button className="primary">
          <FileText /> Crear informe
        </button>
      </div>
      <div className="wide-empty">
        <BarChart3 />
        <h2>Informes mensuales listos</h2>
        <p>
          Selecciona un paciente para revisar métricas, tendencias y adherencia.
        </p>
      </div>
    </>
  );
}
function DoctorSettings() {
  return (
    <>
      <div className="desktop-title">
        <div>
          <p className="eyebrow">Preferencias</p>
          <h1>Configuración</h1>
          <p>Administra notificaciones y datos de la clínica.</p>
        </div>
      </div>
      <div className="settings-panel">
        <h2>Notificaciones clínicas</h2>
        <label>
          <span>
            Alertas críticas en tiempo real
            <small>Recibe avisos ante lecturas fuera del rango seguro.</small>
          </span>
          <input type="checkbox" defaultChecked />
        </label>
        <label>
          <span>
            Resumen diario
            <small>Actividad de pacientes y seguimientos pendientes.</small>
          </span>
          <input type="checkbox" defaultChecked />
        </label>
      </div>
    </>
  );
}

export default App;
