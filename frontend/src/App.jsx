import { useState } from 'react'
import { Button } from '@/components/ui/button.jsx'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card.jsx'
import { Input } from '@/components/ui/input.jsx'
import { Label } from '@/components/ui/label.jsx'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs.jsx'
import { Badge } from '@/components/ui/badge.jsx'
import { Heart, Package, Users, ShoppingCart } from 'lucide-react'
import './App.css'

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [loginData, setLoginData] = useState({ username: '', password: '' })
  const [cestas, setCestas] = useState([])
  const [novaCesta, setNovaCesta] = useState('')

  const handleLogin = async (e) => {
    e.preventDefault()
    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginData),
      })
      
      if (response.ok) {
        const data = await response.json()
        localStorage.setItem('token', data.token)
        setIsLoggedIn(true)
      } else {
        alert('Erro no login. Verifique suas credenciais.')
      }
    } catch (error) {
      console.error('Erro no login:', error)
      alert('Erro de conexão com o servidor.')
    }
  }

  const criarCesta = async () => {
    if (!novaCesta.trim()) return
    
    try {
      const token = localStorage.getItem('token')
      const response = await fetch('http://localhost:8080/api/cestas-basicas', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ nomeCesta: novaCesta }),
      })
      
      if (response.ok) {
        const cesta = await response.json()
        setCestas([...cestas, cesta])
        setNovaCesta('')
      } else {
        alert('Erro ao criar cesta básica.')
      }
    } catch (error) {
      console.error('Erro ao criar cesta:', error)
      alert('Erro de conexão com o servidor.')
    }
  }

  const carregarCestas = async () => {
    try {
      const token = localStorage.getItem('token')
      const response = await fetch('http://localhost:8080/api/cestas-basicas', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      })
      
      if (response.ok) {
        const data = await response.json()
        setCestas(data)
      }
    } catch (error) {
      console.error('Erro ao carregar cestas:', error)
    }
  }

  if (!isLoggedIn) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
        <Card className="w-full max-w-md">
          <CardHeader className="text-center">
            <div className="mx-auto mb-4 w-12 h-12 bg-red-100 rounded-full flex items-center justify-center">
              <Heart className="w-6 h-6 text-red-600" />
            </div>
            <CardTitle className="text-2xl">Sistema de Doações</CardTitle>
            <CardDescription>
              Faça login para gerenciar cestas básicas
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleLogin} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="username">Usuário</Label>
                <Input
                  id="username"
                  type="text"
                  placeholder="Digite seu usuário"
                  value={loginData.username}
                  onChange={(e) => setLoginData({...loginData, username: e.target.value})}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="password">Senha</Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="Digite sua senha"
                  value={loginData.password}
                  onChange={(e) => setLoginData({...loginData, password: e.target.value})}
                  required
                />
              </div>
              <Button type="submit" className="w-full">
                Entrar
              </Button>
            </form>
            <div className="mt-4 p-3 bg-blue-50 rounded-lg">
              <p className="text-sm text-blue-700">
                <strong>Credenciais de teste:</strong><br />
                Usuário: admin<br />
                Senha: admin123
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <Heart className="w-8 h-8 text-red-600 mr-3" />
              <h1 className="text-xl font-semibold text-gray-900">Sistema de Doações</h1>
            </div>
            <Button 
              variant="outline" 
              onClick={() => {
                localStorage.removeItem('token')
                setIsLoggedIn(false)
              }}
            >
              Sair
            </Button>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Tabs defaultValue="cestas" className="space-y-6">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="cestas" className="flex items-center gap-2">
              <Package className="w-4 h-4" />
              Cestas Básicas
            </TabsTrigger>
            <TabsTrigger value="beneficiados" className="flex items-center gap-2">
              <Users className="w-4 h-4" />
              Beneficiados
            </TabsTrigger>
            <TabsTrigger value="doacoes" className="flex items-center gap-2">
              <ShoppingCart className="w-4 h-4" />
              Doações
            </TabsTrigger>
            <TabsTrigger value="relatorios" className="flex items-center gap-2">
              <Heart className="w-4 h-4" />
              Relatórios
            </TabsTrigger>
          </TabsList>

          <TabsContent value="cestas" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Gerenciar Cestas Básicas</CardTitle>
                <CardDescription>
                  Crie e gerencie cestas básicas para distribuição
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="flex gap-4 mb-6">
                  <Input
                    placeholder="Nome da nova cesta básica"
                    value={novaCesta}
                    onChange={(e) => setNovaCesta(e.target.value)}
                    className="flex-1"
                  />
                  <Button onClick={criarCesta}>
                    Criar Cesta
                  </Button>
                  <Button variant="outline" onClick={carregarCestas}>
                    Atualizar Lista
                  </Button>
                </div>

                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                  {cestas.map((cesta) => (
                    <Card key={cesta.id} className="border-l-4 border-l-blue-500">
                      <CardHeader className="pb-3">
                        <div className="flex justify-between items-start">
                          <CardTitle className="text-lg">{cesta.nomeCesta}</CardTitle>
                          <Badge variant={cesta.status === 'Montada' ? 'default' : 'secondary'}>
                            {cesta.status}
                          </Badge>
                        </div>
                        <CardDescription>
                          Data: {new Date(cesta.dataMontagem).toLocaleDateString('pt-BR')}
                        </CardDescription>
                      </CardHeader>
                      <CardContent>
                        <div className="flex gap-2">
                          <Button size="sm" variant="outline">
                            Editar
                          </Button>
                          <Button size="sm" variant="outline">
                            Ver Itens
                          </Button>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>

                {cestas.length === 0 && (
                  <div className="text-center py-8 text-gray-500">
                    <Package className="w-12 h-12 mx-auto mb-4 opacity-50" />
                    <p>Nenhuma cesta básica encontrada.</p>
                    <p className="text-sm">Crie uma nova cesta para começar.</p>
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="beneficiados">
            <Card>
              <CardHeader>
                <CardTitle>Beneficiados</CardTitle>
                <CardDescription>
                  Gerencie os beneficiados do programa
                </CardDescription>
              </CardHeader>
              <CardContent>
                <p className="text-gray-500">Funcionalidade em desenvolvimento...</p>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="doacoes">
            <Card>
              <CardHeader>
                <CardTitle>Doações</CardTitle>
                <CardDescription>
                  Registre e gerencie doações recebidas
                </CardDescription>
              </CardHeader>
              <CardContent>
                <p className="text-gray-500">Funcionalidade em desenvolvimento...</p>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="relatorios">
            <Card>
              <CardHeader>
                <CardTitle>Relatórios</CardTitle>
                <CardDescription>
                  Visualize relatórios e estatísticas
                </CardDescription>
              </CardHeader>
              <CardContent>
                <p className="text-gray-500">Funcionalidade em desenvolvimento...</p>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </main>
    </div>
  )
}

export default App

